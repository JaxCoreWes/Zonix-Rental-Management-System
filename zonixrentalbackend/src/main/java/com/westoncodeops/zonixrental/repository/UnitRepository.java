package com.westoncodeops.zonixrental.repository;

import com.westoncodeops.zonixrental.entities.Unit;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
Optional<Unit> findByUnitNumber(@NotBlank String unitNumber);
    // Correct: Returns a List of Unit entities and takes zero parameters
    List<Unit> findByIsOccupiedFalse();
    Optional<Unit> findByTenantId(UUID tenantId);
}
