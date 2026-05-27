package com.westoncodeops.zonixrental.service.MaintenanceService;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateTicketRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.MaintenanceTicketResponse;
import com.westoncodeops.zonixrental.entities.MaintenanceTicket;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.TicketStatus;
import com.westoncodeops.zonixrental.exceptions.ResourceNotFoundException;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import com.westoncodeops.zonixrental.repository.MaintenanceRepository;
import com.westoncodeops.zonixrental.service.UserService.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService implements IMaintenanceService {

    private final MaintenanceRepository ticketRepository;
    private final IUserService userService;
    private final UnitRepository unitRepository;

    // --- TENANT ACTION: Create a ticket ---
    public MaintenanceTicketResponse createTicket(CreateTicketRequest request) {
        // 1. Find the Tenant
        User tenant = userService.getEntityByPhone(request.phoneNumber());

        // 2. Find the Tenant's Unit (Requires adding findByTenantId to UnitRepository)
        Unit unit = unitRepository.findByTenantId(tenant.getId())
                .orElseThrow(() -> new IllegalStateException("Tenant is not assigned to a unit!"));

        // 3. Mock the AI Priority (Replace this later with your actual AI logic)
        String priority = determinePriorityMock(request.category());

        // Generate a readable number: REQ + Current Time in Millis (shortened)
        // Or REQ + Total Tickets + 1
        String generatedNumber = "MNT-" + System.currentTimeMillis() % 100000;

        // 4. Build and Save
        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .ticketNumber(generatedNumber)
                .category(request.category())
                .description(request.description())
                .status(TicketStatus.PENDING) // Always PENDING on creation
                .aiPriority(priority)
                .reportedAt(LocalDateTime.now())
                .resolvedAt(null) // Null until fixed
                .user(tenant)
                .unit(unit)
                .build();

        return toResponse(ticketRepository.save(ticket));
    }

    // --- CARETAKER ACTION: Mark as Resolved ---
    public MaintenanceTicketResponse resolveTicket(UUID ticketId) {
        MaintenanceTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now()); // Timestamp the fix

        return toResponse(ticketRepository.save(ticket));
    }

    // --- FETCHERS ---
    public List<MaintenanceTicketResponse> getTenantTickets(String phoneNumber) {
        User tenant = userService.getEntityByPhone(phoneNumber);
        return ticketRepository.findByUserId(tenant.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MaintenanceTicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // --- MAPPERS & HELPERS ---
    private MaintenanceTicketResponse toResponse(MaintenanceTicket ticket) {
        String tenantName = ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName();
        return new MaintenanceTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getCategory(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getAiPriority(),
                ticket.getReportedAt(),
                ticket.getResolvedAt(),
                ticket.getUnit().getUnitNumber(),
                tenantName
        );
    }

    private String determinePriorityMock(MaintenanceCategory category) {
        return switch (category) {
            case PLUMBING, ELECTRICAL -> "HIGH";
            case HVAC -> "MEDIUM";
            default -> "LOW";
        };
    }
}