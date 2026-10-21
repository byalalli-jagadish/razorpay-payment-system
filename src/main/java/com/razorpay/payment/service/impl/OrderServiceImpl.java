package com.razorpay.payment.service.impl;

import com.razorpay.common.enums.OrderStatus;
import com.razorpay.common.exception.DuplicateResourceException;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.merchant.entity.Customer;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.repository.CustomerRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.payment.Dto.Request.CreateOrderRequest;
import com.razorpay.payment.Dto.Response.OrderResponse;
import com.razorpay.payment.Dto.Response.PaymentResponse;
import com.razorpay.payment.entity.OrderRecord;
import com.razorpay.payment.repository.OrderRepository;
import com.razorpay.payment.repository.PaymentRepository;
import com.razorpay.payment.mapper.PaymentMapper;
import com.razorpay.payment.mapper.OrderMapper;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if (request.receipt() != null
                && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new DuplicateResourceException(
                    "ORDER_RECEIPT_DUPLICATE",
                    "Order with receipt already exists: " + request.receipt());
        }

        UUID customerId = null;
        if (request.customer() != null) {
            CreateOrderRequest.CustomerDetails customerDetails = request.customer();
            Customer customer = customerRepository
                    .findByMerchant_IdAndEmail(merchantId, customerDetails.email())
                    .orElseGet(() -> {
                        Merchant merchant = merchantRepository.findById(merchantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));
                        return customerRepository.save(Customer.builder()
                                .merchant(merchant)
                                .name(customerDetails.name())
                                .email(customerDetails.email())
                                .phone(customerDetails.phone())
                                .build());
                    });
            customerId = customer.getId();
        }

        OrderRecord order = orderRepository.save(OrderRecord.builder()
                .merchantId(merchantId)
                .customerId(customerId)
                .amount(request.amount())
                .receipt(request.receipt())
                .notes(request.notes())
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null
                        ? request.expiresAt()
                        : LocalDateTime.now().plusMinutes(defaultExpiryMinutes))
                .build());

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getOrderStatus() == OrderStatus.PAID
                || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleViolationException(
                    "ORDER_CANNOT_CANCEL",
                    "Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        List<Payment> payments = paymentRepository.findByOrder_IdAndMerchantId(orderId, merchantId);
        return paymentMapper.toResponseList(payments);
    }

}
