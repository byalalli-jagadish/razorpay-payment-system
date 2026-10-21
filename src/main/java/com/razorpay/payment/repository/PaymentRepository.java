package com.razorpay.payment.repository;

import com.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import com.razorpay.common.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_IdAndMerchantId(UUID orderId, UUID merchantId);
    boolean existsByMerchantIdAndIdempotencyKey(UUID merchantId, String idempotencyKey);
    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime createdAt);
}
