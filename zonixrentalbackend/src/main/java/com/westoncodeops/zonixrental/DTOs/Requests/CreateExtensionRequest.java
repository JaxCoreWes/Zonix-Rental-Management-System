package com.westoncodeops.zonixrental.DTOs.Requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;

public record CreateExtensionRequest(@NotBlank String phoneNumber,
                                     @NotNull @Future LocalDate expectedPaymentDate,
                                     @Nullable String reason) {
}
