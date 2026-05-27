package com.westoncodeops.zonixrental.repository;

import com.westoncodeops.zonixrental.entities.MaintenanceTicket;
import com.westoncodeops.zonixrental.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRepository extends JpaRepository<MaintenanceTicket, UUID> {
    Optional<MaintenanceTicket> findByUserId(UUID userId);
    long countByStatus(TicketStatus status);

}