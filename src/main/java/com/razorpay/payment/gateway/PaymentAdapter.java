package com.razorpay.payment.gateway;

import com.razorpay.payment.gateway.dto.PaymentRequest;
import com.razorpay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest request);
    PaymentResult capture(UUID paymentId);
}
