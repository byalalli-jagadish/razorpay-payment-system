package com.razorpay.payment.service;

import com.razorpay.payment.Dto.Request.CreateOrderRequest;
import com.razorpay.payment.Dto.Response.OrderResponse;
import com.razorpay.payment.Dto.Response.PaymentResponse;

import java.util.UUID;
import java.util.List;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
    OrderResponse getById(UUID merchantId, UUID orderId);
    OrderResponse cancel(UUID merchantId, UUID orderId);
    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
