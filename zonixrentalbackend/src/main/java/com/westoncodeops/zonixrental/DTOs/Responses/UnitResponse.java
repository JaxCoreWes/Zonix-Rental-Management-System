package com.westoncodeops.zonixrental.DTOs.Responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UnitResponse(Long id,
                           String unitNumber,
                           BigDecimal rentAmount,
                           Integer floor,
                           LocalDateTime createdAt,
                           Boolean isOccupied,
                           String tenantName) {
}
