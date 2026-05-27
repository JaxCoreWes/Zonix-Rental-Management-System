package com.westoncodeops.zonixrental.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(@NotNull Long unitId,
                             @NotNull BigDecimal amount,
                             @NotBlank String mpesaRef,
                             String payersPhoneNumber) {
}
