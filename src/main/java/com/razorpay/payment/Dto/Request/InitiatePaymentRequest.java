package com.razorpay.payment.Dto.Request;

import com.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record InitiatePaymentRequest(
        @NotNull(message = "Order Id is required") UUID orderId,
        @NotNull PaymentMethod method,
        Map<String, Object> methodDetails
) {}
