package com.razorpay.merchant.Dto.Request;

import com.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
