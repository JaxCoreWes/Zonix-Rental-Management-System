package com.westoncodeops.zonixrental.DTOs.Requests;

import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest( @NotBlank String phoneNumber,
                                   @NotNull MaintenanceCategory category,
                                   @NotBlank String description) {
}
