package com.westoncodeops.zonixrental.repository;

import com.westoncodeops.zonixrental.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
Optional<Payment> findTopByUnitIdOrderByCoversUntilDesc(Long Id);
    // Returns the sum of all completed payments for a specific unit within a date range
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.unit.id = :unitId AND p.status = 'COMPLETED' AND p.paymentDate >= :startOfMonth AND p.paymentDate <= :endOfMonth")
    BigDecimal sumCompletedPaymentsForUnitInDateRange(
            @Param("unitId") Long unitId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );
}
