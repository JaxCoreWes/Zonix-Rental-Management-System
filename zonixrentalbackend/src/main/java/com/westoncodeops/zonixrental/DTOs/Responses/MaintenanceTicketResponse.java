package com.westoncodeops.zonixrental.DTOs.Responses;

import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import com.westoncodeops.zonixrental.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceTicketResponse(UUID id,
                                        String ticketNumber,
                                        MaintenanceCategory category,
                                        String description,
                                        TicketStatus status,
                                        String aiPriority,
                                        LocalDateTime reportedAt,
                                        LocalDateTime resolvedAt,
                                        String tenantName,
                                        String unitNumber) {
}
