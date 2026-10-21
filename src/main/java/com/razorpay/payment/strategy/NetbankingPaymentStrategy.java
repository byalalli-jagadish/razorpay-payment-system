package com.razorpay.payment.strategy;

import com.razorpay.common.enums.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class NetbankingPaymentStrategy extends AbstractPaymentInitiationStrategy {
    @Override public PaymentMethod supports() { return PaymentMethod.NETBANKING; }
}
