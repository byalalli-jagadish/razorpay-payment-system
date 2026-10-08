package com.razorpay.merchant.controller;

import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import com.razorpay.merchant.service.ApikeyService;
import com.razorpay.merchant.service.impl.ApikeyServiceimpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/merchant/{merchantid}/api-keys")
public class ApikeyController {

    private final ApikeyService apikeyService;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@PathVariable UUID merchantid , @Valid @RequestBody CreateApiKeyRequest request) {
      return  ResponseEntity.status(HttpStatus.CREATED).body(apikeyService.create(merchantid, request));
    }

}
