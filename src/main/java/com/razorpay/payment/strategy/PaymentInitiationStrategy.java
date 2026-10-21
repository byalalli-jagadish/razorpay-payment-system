package com.razorpay.payment.strategy;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.Dto.Request.InitiatePaymentRequest;

import java.util.Map;

public interface PaymentInitiationStrategy {
    PaymentMethod supports();
    Map<String, Object> initiate(InitiatePaymentRequest request);
}
