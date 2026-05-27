package com.westoncodeops.zonixrental.entities;

import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import com.westoncodeops.zonixrental.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="maintenance_tickets")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String ticketNumber; // This is for the UI/Tenant

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceCategory category;

    @Lob
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private String aiPriority;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Column
    private LocalDateTime resolvedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;



}
