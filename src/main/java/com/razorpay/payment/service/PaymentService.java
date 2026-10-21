package com.razorpay.payment.service;

import com.razorpay.payment.Dto.Request.InitiatePaymentRequest;
import com.razorpay.payment.Dto.Response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, InitiatePaymentRequest request);
    void resolveAuthorization(UUID paymentId, boolean approve, String bankRef,
                              String errorCode, String errorDescription);
}
