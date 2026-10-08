package com.razorpay.merchant.service.impl;

import com.razorpay.merchant.Dto.Request.MerchantSignupRequest;
import com.razorpay.merchant.Dto.Response.MerchantResponse;
import com.razorpay.common.enums.MerchantStatus;
import com.razorpay.common.enums.UserRole;
import com.razorpay.common.exception.DuplicateResourceException;
import com.razorpay.merchant.entity.AppUser;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.mapper.MerchantMapper;
import com.razorpay.merchant.repository.AppUSerRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUSerRepository appUserRepository;
    private final MerchantMapper merchantMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "DUPLICATE_MERCHANT_EMAIL",
                    "Merchant with email already exists: " + request.email());
        }

        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);
        merchant = merchantRepository.save(merchant);

        appUserRepository.save(AppUser.builder()
                .merchant(merchant)
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build());

        return merchantMapper.toResponse(merchant);
    }
}
