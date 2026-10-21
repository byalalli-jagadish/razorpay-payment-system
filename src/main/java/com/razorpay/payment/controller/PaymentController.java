package com.razorpay.payment.controller;

import com.razorpay.merchant.security.MerchantContext;
import com.razorpay.payment.Dto.Request.InitiatePaymentRequest;
import com.razorpay.payment.Dto.Response.PaymentResponse;
import com.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final MerchantContext merchantContext;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantContext.getMerchantId(), request));
    }
}
