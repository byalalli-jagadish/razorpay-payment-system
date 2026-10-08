package com.razorpay.merchant.mapper;

import com.razorpay.merchant.Dto.Request.MerchantSignupRequest;
import com.razorpay.merchant.Dto.Response.MerchantResponse;
import com.razorpay.merchant.entity.Merchant;
import org.springframework.stereotype.Component;

@Component
public class MerchantMapper {

    public Merchant toEntityFromSignUpRequest(MerchantSignupRequest request) {
        return Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessName(request.businessName())
                .businessType(request.businessType())
                .build();
    }

    public MerchantResponse toResponse(Merchant merchant) {
        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getEmail(),
                merchant.getBusinessName(),
                merchant.getBusinessType(),
                merchant.getStatus());
    }
}
