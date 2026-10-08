package com.razorpay.merchant.controller;

import com.razorpay.merchant.Dto.Request.MerchantSignupRequest;
import com.razorpay.merchant.Dto.Response.MerchantResponse;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@Valid @RequestBody MerchantSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }
}
