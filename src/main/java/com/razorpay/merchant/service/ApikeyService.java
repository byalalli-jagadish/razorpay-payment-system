package com.razorpay.merchant.service;

import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import com.razorpay.merchant.Dto.Response.ApiKeyResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public interface ApikeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}
