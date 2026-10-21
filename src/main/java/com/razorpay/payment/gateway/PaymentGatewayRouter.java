package com.razorpay.payment.gateway;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.gateway.dto.PaymentRequest;
import com.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {
    private final Map<String, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest request) {
        PaymentAdapter adapter = paymentAdapters.get(request.method().name() + "_ADAPTER");
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter registered for method: " + request.method());
        }
        return adapter.initiate(request);
    }

    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapters.get(method.name() + "_ADAPTER");
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter registered for method: " + method);
        }
        return adapter.capture(paymentId);
    }
}
