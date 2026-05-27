package com.westoncodeops.zonixrental.DTOs.Responses;

import com.westoncodeops.zonixrental.enums.ExtensionStatus;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExtensionResponse(UUID id,
                                String tenantName,
                                String uniName,
                                LocalDate expectedPaymentDate,
                                String reason,
                                ExtensionStatus status,
                                String reviewedBy,
                                LocalDateTime createdAt) {
}
