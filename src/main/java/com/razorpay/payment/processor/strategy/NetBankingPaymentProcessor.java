package com.razorpay.payment.processor.strategy;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.payment.processor.PaymentProcessor;
import com.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component("NETBANKING_PROCESSOR")
public class NetBankingPaymentProcessor implements PaymentProcessor {
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        Object bank = request.methodDetails() == null ? null : request.methodDetails().get("bank");
        if ("BANK_CODE_FAIL".equals(bank))
            return new PaymentProcessorResponse.Failure("BANK_REJECTED", "Bank rejected the transaction registration");
        return new PaymentProcessorResponse.Pending("NBK_PROCESSOR_" + RandomizerUtil.randomBase64(16));
    }
}
