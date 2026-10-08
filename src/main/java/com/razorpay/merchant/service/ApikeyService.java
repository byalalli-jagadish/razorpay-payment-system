package com.razorpay.merchant.service;

import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ApikeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);
}
