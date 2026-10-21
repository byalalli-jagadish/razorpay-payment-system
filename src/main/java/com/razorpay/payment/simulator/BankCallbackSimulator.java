package com.razorpay.payment.simulator;

import com.razorpay.common.enums.ChaosMode;
import com.razorpay.common.enums.PaymentStatus;
import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.repository.PaymentRepository;
import com.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallbackSimulator {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    public void processCallbacks() {
        List<Payment> candidates = paymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, LocalDateTime.now().minusSeconds(1));
        for (Payment payment : candidates) {
            if (simulatorConfig.getChaosMode() == ChaosMode.FAILURE) {
                paymentService.resolveAuthorization(payment.getId(), false, null,
                        "SIM_BANK_ERROR_CODE", "Simulated Bank Decline");
            } else if (simulatorConfig.getChaosMode() != ChaosMode.TIMEOUT) {
                paymentService.resolveAuthorization(payment.getId(), true,
                        "SIM_BANK_REF" + RandomizerUtil.randomBase64(8), null, null);
            }
        }
    }
}
