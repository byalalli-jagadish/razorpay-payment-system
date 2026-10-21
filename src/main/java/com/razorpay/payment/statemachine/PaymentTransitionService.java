package com.razorpay.payment.statemachine;

import com.razorpay.common.enums.PaymentActor;
import com.razorpay.common.enums.PaymentEvent;
import com.razorpay.common.enums.PaymentStatus;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.entity.PaymentTransitionLog;
import com.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {
    private final PaymentTransitionLogRepository logRepository;
    private final PaymentStateMachine stateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = stateMachine.transition(payment.getStatus(), event);
        logRepository.save(PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build());
        payment.setStatus(next);
        return next;
    }
}
