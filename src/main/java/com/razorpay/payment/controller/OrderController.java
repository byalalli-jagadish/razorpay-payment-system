package com.razorpay.payment.controller;

import com.razorpay.merchant.security.MerchantContext;
import com.razorpay.payment.Dto.Request.CreateOrderRequest;
import com.razorpay.payment.Dto.Response.OrderResponse;
import com.razorpay.payment.Dto.Response.PaymentResponse;
import com.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final MerchantContext merchantContext;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getById(@PathVariable UUID orderId) {
        return orderService.getById(merchantContext.getMerchantId(), orderId);
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable UUID orderId) {
        return orderService.cancel(merchantContext.getMerchantId(), orderId);
    }

    @GetMapping("/{orderId}/payments")
    public List<PaymentResponse> listPayments(@PathVariable UUID orderId) {
        return orderService.listPayments(merchantContext.getMerchantId(), orderId);
    }
}
