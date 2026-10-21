package com.razorpay.payment.service.impl;

import com.razorpay.common.enums.OrderStatus;
import com.razorpay.common.enums.PaymentStatus;
import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.payment.Dto.Request.InitiatePaymentRequest;
import com.razorpay.payment.Dto.Response.PaymentResponse;
import com.razorpay.payment.entity.OrderRecord;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.mapper.PaymentMapper;
import com.razorpay.payment.gateway.PaymentGatewayRouter;
import com.razorpay.payment.gateway.dto.PaymentRequest;
import com.razorpay.payment.gateway.dto.PaymentResult;
import com.razorpay.common.enums.PaymentEvent;
import com.razorpay.payment.statemachine.PaymentTransitionService;
import com.razorpay.common.enums.PaymentEvent;
import com.razorpay.payment.repository.OrderRepository;
import com.razorpay.payment.repository.PaymentRepository;
import com.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, InitiatePaymentRequest request) {
        UUID orderId = request.orderId();
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.PAID) {
            throw new BusinessRuleViolationException("ORDER_PAYMENT_NOT_ALLOWED",
                    "Cannot initiate payment for order with status: " + order.getOrderStatus());
        }
        if (order.getExpiresAt() != null && order.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessRuleViolationException("ORDER_EXPIRED", "Order has expired: " + orderId);
        }
        Payment payment = paymentRepository.save(Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .idempotencyKey(UUID.randomUUID().toString())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build());

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);

        PaymentResult result = paymentGatewayRouter.initiate(new PaymentRequest(
                payment.getId(), orderId, merchantId, order.getAmount(),
                request.method(), request.methodDetails()));

        if (result instanceof PaymentResult.Pending pending) {
            payment.setProcessorReference(pending.registrationRef());
        } else if (result instanceof PaymentResult.Failure failure) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
        } else if (result instanceof PaymentResult.Success success) {
            payment.setStatus(PaymentStatus.AUTHORIZED);
            payment.setBankReference(success.bankReference());
        }
        payment = paymentRepository.save(payment);

        order.setAttempts(order.getAttempts() + 1);
        order.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRepository.save(order);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef,
                                     String errorCode, String errorDescription) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        if (payment.getStatus() != PaymentStatus.AUTHORIZING) return;
        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }
        paymentRepository.save(payment);
    }
}
