package com.razorpay.vault.service.impl;

import com.razorpay.common.Util.RandomizerUtil;
import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.CardBrand;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.payment.processor.PaymentProcessorRouter;
import com.razorpay.payment.processor.dto.*;
import com.razorpay.vault.config.VaultEncryptionConfig;
import com.razorpay.vault.dto.request.TokenizeRequest;
import com.razorpay.vault.dto.response.TokenizeResponse;
import com.razorpay.vault.entity.*;
import com.razorpay.vault.repository.*;
import com.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {
    private final CardTokenRepository tokenRepository;
    private final VaultCardRepository cardRepository;
    private final BytesEncryptor vaultDekEncryptor;
    private final PaymentProcessorRouter processorRouter;

    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest r, UUID merchantId) {
        String lastFour = r.pan().substring(r.pan().length() - 4);
        CardBrand brand = r.pan().startsWith("4") ? CardBrand.VISA :
                (r.pan().startsWith("5") || r.pan().startsWith("2") ? CardBrand.MASTERCARD : CardBrand.RUPAY);
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek).encrypt(r.pan().getBytes(StandardCharsets.UTF_8));
        VaultCard card = cardRepository.save(VaultCard.builder().lastFour(lastFour)
                .bin(r.pan().substring(0, 6)).brand(brand).encryptedPan(encryptedPan)
                .encryptedDek(vaultDekEncryptor.encrypt(dek)).expiryMonth(r.expiryMonth().toString())
                .expiryYear(r.expiryYear().toString()).cardHolderName(r.cardHolderName()).build());
        String token = "tok_" + RandomizerUtil.randomBase64(32);
        tokenRepository.save(CardToken.builder().vaultCard(card).token(token)
                .customer(r.customerId()).merchant(merchantId).build());
        return new TokenizeResponse(token, lastFour, brand, r.expiryMonth(), r.expiryYear());
    }

    @Transactional
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> details) {
        CardToken cardToken = tokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));
        byte[] panBytes = null;
        try {
            byte[] dek = vaultDekEncryptor.decrypt(cardToken.getVaultCard().getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(cardToken.getVaultCard().getEncryptedPan());
            String pan = new String(panBytes, StandardCharsets.UTF_8);
            PaymentProcessorRequest request = PaymentProcessorRequest.card(paymentId, pan,
                    cardToken.getVaultCard().getExpiryMonth() + "/" + cardToken.getVaultCard().getExpiryYear(), amount, details);
            return processorRouter.charge(request);
        } catch (Exception e) {
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally { if (panBytes != null) Arrays.fill(panBytes, (byte) 0); }
    }
}
