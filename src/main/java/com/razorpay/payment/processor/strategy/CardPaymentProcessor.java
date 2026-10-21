package com.razorpay.payment.processor.strategy;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.payment.processor.PaymentProcessor;
import com.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component("CARD_PROCESSOR")
public class CardPaymentProcessor implements PaymentProcessor {
    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000069";

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        if (PAN_CARD_DECLINED.equals(request.pan()))
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by bank");
        if (PAN_CARD_EXPIRED.equals(request.pan()))
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        return new PaymentProcessorResponse.Pending("CARD_PROCESSOR_" + RandomizerUtil.randomBase64(16));
    }
}
