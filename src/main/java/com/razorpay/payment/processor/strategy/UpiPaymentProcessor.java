package com.razorpay.payment.processor.strategy;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.processor.PaymentProcessor;
import com.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component("UPI_PROCESSOR")
public class UpiPaymentProcessor implements PaymentProcessor {
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        Object vpa = request.methodDetails() == null ? null : request.methodDetails().get("vpa");
        if ("fail@okaxis".equals(vpa))
            return new PaymentProcessorResponse.Failure("UPI_REJECTED", "Bank rejected the transaction registration");
        return new PaymentProcessorResponse.Pending("UPI_PROCESSOR_" + RandomizerUtil.randomBase64(16));
    }
}
