package com.razorpay.vault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class VaultEncryptionConfig {
    @Bean
    public BytesEncryptor vaultDekEncryptor() {
        byte[] key = KeyGenerators.secureRandom(32).generateKey();
        return new AesBytesEncryptor(new SecretKeySpec(key, "AES"),
                KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }

    public static BytesEncryptor panEncryptor(byte[] dek) {
        return new AesBytesEncryptor(new SecretKeySpec(dek, "AES"),
                KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
