package com.razorpay.payment.strategy;

import com.razorpay.payment.Dto.Request.InitiatePaymentRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractPaymentInitiationStrategy implements PaymentInitiationStrategy {
    @Override
    public Map<String, Object> initiate(InitiatePaymentRequest request) {
        Map<String, Object> details = new HashMap<>();
        if (request.methodDetails() != null) {
            details.putAll(request.methodDetails());
        }
        details.put("processorReference", "pay_" + UUID.randomUUID());
        details.put("method", supports().name());
        return details;
    }
}
