package com.westoncodeops.zonixrental.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateUnitRequest(@NotBlank String unitNumber,
                                @NotNull BigDecimal rentAmount,
                                @NotNull Integer Floor) {
}
