package com.razorpay.merchant.service;

import com.razorpay.merchant.Dto.Request.MerchantSignupRequest;
import com.razorpay.merchant.Dto.Response.MerchantResponse;
import com.razorpay.merchant.Dto.Request.LoginRequest;
import com.razorpay.merchant.Dto.Response.LoginResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
    LoginResponse login(LoginRequest request);
}
