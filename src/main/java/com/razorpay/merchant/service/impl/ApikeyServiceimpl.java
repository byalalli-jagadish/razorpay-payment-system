package com.razorpay.merchant.service.impl;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.repository.ApikeyRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.ApikeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApikeyServiceimpl implements ApikeyService {
    private final ApikeyRepository apikeyRepository;
    private final MerchantRepository merchantRepository;
    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(()->new ResourceNotFoundException("merchant",merchantId));

        String keyId = "rzp_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder().merchant(merchant).keyId(keyId).keySecretHash(rawSecret).environment(request.environment()).build();

        apiKey=apikeyRepository.save(apiKey);
      return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }
}
