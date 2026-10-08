package com.razorpay.merchant.Dto.Response;

import com.razorpay.common.enums.BusinessType;
import com.razorpay.common.enums.MerchantStatus;
import lombok.Data;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
