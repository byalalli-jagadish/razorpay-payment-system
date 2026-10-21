package com.razorpay.merchant.service.impl;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import com.razorpay.merchant.Dto.Response.ApiKeyResponse;
import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.repository.ApikeyRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.ApikeyService;
import com.razorpay.merchant.cache.ApiKeyCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApikeyServiceimpl implements ApikeyService {
    private final ApikeyRepository apikeyRepository;
    private final MerchantRepository merchantRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ApiKeyCache apiKeyCache;
    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(()->new ResourceNotFoundException("merchant",merchantId));

        String keyId = "rzp_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(passwordEncoder.encode(rawSecret))
                .environment(request.environment())
                .build();

        apiKey=apikeyRepository.save(apiKey);
      return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        return apikeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apiKey -> new ApiKeyResponse(
                        apiKey.getId(),
                        apiKey.getKeyId(),
                        apiKey.getEnvironment(),
                        apiKey.isEnabled(),
                        apiKey.getLastUsedAt(),
                        apiKey.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apikeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        apiKey.setEnabled(false);
        apikeyRepository.save(apiKey);
        apiKeyCache.evict(apiKey.getKeyId());
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apikeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        if (!apiKey.isEnabled()) {
            throw new IllegalStateException("Cannot rotate a disabled API key");
        }

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(passwordEncoder.encode(newRawSecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apikeyRepository.save(apiKey);
        apiKeyCache.evict(apiKey.getKeyId());

        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                newRawSecret,
                apiKey.getEnvironment());
    }
}
