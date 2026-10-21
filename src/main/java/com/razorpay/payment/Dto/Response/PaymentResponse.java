package com.razorpay.payment.Dto.Response;

import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        String idempotencyKey,
        PaymentStatus status,
        PaymentMethod method,
        String bankReference,
        String processorReference,
        LocalDateTime createdAt,
        LocalDateTime capturedAt
) {}
