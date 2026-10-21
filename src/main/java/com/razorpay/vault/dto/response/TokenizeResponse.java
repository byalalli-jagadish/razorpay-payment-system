package com.razorpay.vault.dto.response;

import com.razorpay.common.enums.CardBrand;

public record TokenizeResponse(String token, String lastFour, CardBrand brand,
                               Integer expiryMonth, Integer expiryYear) {}
