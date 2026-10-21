package com.razorpay.payment.gateway.adapter;

import com.razorpay.payment.gateway.PaymentAdapter;
import com.razorpay.payment.gateway.dto.PaymentRequest;
import com.razorpay.payment.gateway.dto.PaymentResult;
import com.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("CARD_ADAPTER")
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {
    private final VaultService vaultService;
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = request.methodDetails() == null ? null : (String) request.methodDetails().get("token");
        return convert(vaultService.charge(request.paymentId(), token, request.amount(), request.methodDetails()));
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF_" + paymentId);
    }

    private PaymentResult convert(PaymentProcessorResponse result) {
        if (result instanceof PaymentProcessorResponse.Pending p) return new PaymentResult.Pending(p.processorReference());
        if (result instanceof PaymentProcessorResponse.Success s) return new PaymentResult.Success(s.bankReference());
        PaymentProcessorResponse.Failure f = (PaymentProcessorResponse.Failure) result;
        return new PaymentResult.Failure(f.errorCode(), f.errorDescription());
    }
}
