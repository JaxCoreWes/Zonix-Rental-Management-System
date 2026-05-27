package com.westoncodeops.zonixrental.controllers;


import com.westoncodeops.zonixrental.DTOs.Requests.CreateTicketRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.MaintenanceTicketResponse;
import com.westoncodeops.zonixrental.service.MaintenanceService.IMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
public class MaintenanceController {

    public final IMaintenanceService maintenanceService;

    // Tenant Report Window (Chatbot)
    @PostMapping("/requestTicket")
    public ResponseEntity<MaintenanceTicketResponse> reportIssue(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.createTicket(request));
    }

    // Get all maintenance ticket
    @GetMapping("/allTickets")
    public ResponseEntity<List<MaintenanceTicketResponse>> getAllTickets(){
        return ResponseEntity.status(HttpStatus.OK).body(maintenanceService.getAllTickets());
    }

    // Caretaker Action
    @PatchMapping("/{ticketId}/resolve")
    public ResponseEntity<MaintenanceTicketResponse> resolveTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(maintenanceService.resolveTicket(ticketId));
    }





}
