package com.razorpay.vault.controller;

import com.razorpay.merchant.security.MerchantContext;
import com.razorpay.vault.dto.request.TokenizeRequest;
import com.razorpay.vault.dto.response.TokenizeResponse;
import com.razorpay.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {
    private final VaultService vaultService;
    private final MerchantContext merchantContext;

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vaultService.tokenize(request, merchantContext.getMerchantId()));
    }
}
