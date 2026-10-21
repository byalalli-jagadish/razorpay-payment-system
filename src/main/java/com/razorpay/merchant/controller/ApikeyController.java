package com.razorpay.merchant.controller;

import com.razorpay.merchant.Dto.Request.CreateApiKeyRequest;
import com.razorpay.merchant.Dto.Response.ApiKeyCreateResponse;
import com.razorpay.merchant.Dto.Response.ApiKeyResponse;
import com.razorpay.merchant.service.ApikeyService;
import com.razorpay.merchant.security.MerchantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/merchants/api-keys")
public class ApikeyController {

    private final ApikeyService apikeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody CreateApiKeyRequest request) {
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(apikeyService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant() {
        return ResponseEntity.ok(apikeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId) {
        apikeyService.revoke(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID keyId) {
        return ResponseEntity.ok(apikeyService.rotate(merchantContext.getMerchantId(), keyId));
    }

}
