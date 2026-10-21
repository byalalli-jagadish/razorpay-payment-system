package com.razorpay.vault.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;
import java.util.UUID;

public record TokenizeRequest(
        @NotBlank @LuhnCheck @Pattern(regexp = "^[0-9]{13,19}$") String pan,
        @NotBlank @Pattern(regexp = "^[0-9]{3,4}$") String cvv,
        @NotNull @Min(1) @Max(12) Integer expiryMonth,
        @NotNull @Min(2026) Integer expiryYear,
        UUID customerId,
        @Size(min = 3) String cardHolderName
) {}
