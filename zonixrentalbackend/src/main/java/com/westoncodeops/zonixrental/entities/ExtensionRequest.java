package com.westoncodeops.zonixrental.entities;

import com.westoncodeops.zonixrental.enums.ExtensionStatus;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="extension_requets")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    // The new date they are promising to pay by (e.g., the 15th instead of the 5th)
    @Column(nullable = false)
    private LocalDate expectedPaymentDate;

    @Lob
    private String reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExtensionStatus status; // PENDING, APPROVED, REJECTED

    // The Caretaker who clicked approve/deny. Nullable because it starts as pending.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt; // The exact time they submitted the request
}
