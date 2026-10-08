package com.razorpay.merchant.service;

import com.razorpay.merchant.Dto.Request.MerchantSignupRequest;
import com.razorpay.merchant.Dto.Response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
