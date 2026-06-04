package com.westoncodeops.zonixrental.DTOs.Responses;

import com.westoncodeops.zonixrental.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(UUID id,
                              BigDecimal amount,
                              String mpesaReceiptNumber,
                              PaymentStatus status,
                              LocalDateTime paymentDate,
                              LocalDate coversFrom,
                              LocalDate coversUntil,
                              String tenantName,
                              String unitNumber,
                              String phoneNumber,
                              String paymentMethod,
                              String paymentFor
                              ) {
}
