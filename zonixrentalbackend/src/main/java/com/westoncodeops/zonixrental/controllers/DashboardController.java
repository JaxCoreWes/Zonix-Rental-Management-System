package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.entities.Payment;
import com.westoncodeops.zonixrental.enums.ExtensionStatus;
import com.westoncodeops.zonixrental.enums.Role;
import com.westoncodeops.zonixrental.enums.TicketStatus;
import com.westoncodeops.zonixrental.repository.UserRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.repository.MaintenanceRepository;
import com.westoncodeops.zonixrental.repository.PaymentRepository;
import com.westoncodeops.zonixrental.repository.ExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired private UnitRepository unitRepository;
    @Autowired private MaintenanceRepository ticketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ExtensionRepository extensionRepository;

    @GetMapping("/metrics")
    public ResponseEntity<?> getDashboardMetrics(@RequestParam String role) {
        if ("LANDLORD".equalsIgnoreCase(role)) {
            BigDecimal totalRevenue = paymentRepository.findAll().stream()
                    .map(Payment::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long pendingExtensions = extensionRepository.countByStatus(ExtensionStatus.PENDING);
            long totalUnits = unitRepository.count();
            long occupiedUnits = unitRepository.findAll().stream()
                    .filter(unit -> Boolean.TRUE.equals(unit.getIsOccupied()))
                    .count();
            long availableUnits = totalUnits - occupiedUnits;
            long totalTenants = userRepository.countByRole(Role.TENANT);
            long openTickets = ticketRepository.countByStatus(TicketStatus.PENDING);

            return ResponseEntity.ok(Map.of(
                    "totalRevenue", totalRevenue,
                    "pendingExtensions", pendingExtensions,
                    "totalUnits", totalUnits,
                    "occupiedUnits", occupiedUnits,
                    "availableUnits", availableUnits,
                    "totalTenants", totalTenants,
                    "openTickets", openTickets,
                    "systemAlerts", "All financial streams stable."
            ));
        } else if ("CARETAKER".equalsIgnoreCase(role)) {
            long totalTenants = userRepository.countByRole(Role.TENANT);
            long totalUnits = unitRepository.count();
            long occupiedUnits = unitRepository.findAll().stream()
                    .filter(unit -> Boolean.TRUE.equals(unit.getIsOccupied()))
                    .count();
            long availableUnits = totalUnits - occupiedUnits;
            long pendingTickets = ticketRepository.countByStatus(TicketStatus.PENDING);
            long resolvedTickets = ticketRepository.countByStatus(TicketStatus.RESOLVED);
            long pendingExtensions = extensionRepository.countByStatus(ExtensionStatus.PENDING);

            return ResponseEntity.ok(Map.of(
                    "totalTenants", totalTenants,
                    "totalUnits", totalUnits,
                    "availableUnits", availableUnits,
                    "occupiedUnits", occupiedUnits,
                    "pendingTickets", pendingTickets,
                    "resolvedTickets", resolvedTickets,
                    "pendingExtensions", pendingExtensions,
                    "maintenanceStatus", "Operational dashboard live."
            ));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Unknown role specified"));
    }
}