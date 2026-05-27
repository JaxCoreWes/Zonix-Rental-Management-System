package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.DTOs.Responses.MaintenanceTicketResponse;
import com.westoncodeops.zonixrental.service.MaintenanceService.IMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
public class MaintenancePublicController {

    private final IMaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<MaintenanceTicketResponse>> getMaintenanceTickets() {
        return ResponseEntity.ok(maintenanceService.getAllTickets());
    }

    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<MaintenanceTicketResponse> resolveTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(maintenanceService.resolveTicket(ticketId));
    }
}
