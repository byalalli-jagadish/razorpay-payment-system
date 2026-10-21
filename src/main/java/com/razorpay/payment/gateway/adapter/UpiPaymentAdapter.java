package com.razorpay.payment.gateway.adapter;

import com.razorpay.payment.gateway.PaymentAdapter;
import com.razorpay.payment.gateway.dto.PaymentRequest;
import com.razorpay.payment.gateway.dto.PaymentResult;
import com.razorpay.payment.processor.PaymentProcessorRouter;
import com.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("UPI_ADAPTER")
@RequiredArgsConstructor
public class UpiPaymentAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter processorRouter;
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        return convert(processorRouter.charge(PaymentProcessorRequest.nonCard(request.paymentId(), request.method(), request.amount(), request.methodDetails())));
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF_" + paymentId);
    }

    private PaymentResult convert(PaymentProcessorResponse result) {
        if (result instanceof PaymentProcessorResponse.Pending p) return new PaymentResult.Pending(p.processorReference());
        if (result instanceof PaymentProcessorResponse.Success s) return new PaymentResult.Success(s.bankReference());
        PaymentProcessorResponse.Failure f = (PaymentProcessorResponse.Failure) result;
        return new PaymentResult.Failure(f.errorCode(), f.errorDescription());
    }
}
