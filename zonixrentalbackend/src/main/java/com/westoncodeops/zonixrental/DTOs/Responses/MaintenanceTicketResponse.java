package com.westoncodeops.zonixrental.DTOs.Responses;

import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import com.westoncodeops.zonixrental.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceTicketResponse(UUID id,
                                        String ticketNumber,
                                        String title,
                                        MaintenanceCategory category,
                                        String description,
                                        TicketStatus status,
                                        String aiPriority,
                                        String priority,
                                        LocalDateTime reportedAt,
                                        LocalDateTime resolvedAt,
                                        String tenantName,
                                        String reportedByName,
                                        String unitNumber) {
    // Compact constructor to set title and priority from existing fields
    public MaintenanceTicketResponse {
        title = ticketNumber;
        priority = aiPriority;
        reportedByName = tenantName;
    }
}
