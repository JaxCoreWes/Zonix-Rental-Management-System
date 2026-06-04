package com.westoncodeops.zonixrental.service.UnitService;


import com.westoncodeops.zonixrental.DTOs.Requests.AssignTenantRequest;
import com.westoncodeops.zonixrental.DTOs.Requests.CreateUnitRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UnitResponse;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.enums.Role;
import com.westoncodeops.zonixrental.exceptions.ResourceNotFoundException;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    @Override
    public UnitResponse createUnit(CreateUnitRequest request) {
        if (unitRepository.findByUnitNumber(request.unitNumber()).isPresent()) {
            throw new RuntimeException("Unit already exists");
        }

        Unit newUnit = Unit.builder()
                .unitNumber(request.unitNumber())
                .rentAmount(request.rentAmount())
                .floor(request.floor())
                .isOccupied(false)
                .tenant(null)
                .createdAt(LocalDateTime.now())
                .build();

        Unit savedUnit = unitRepository.save(newUnit);
        return toResponse(savedUnit);
    }

    @Override
    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<UnitResponse> getVacantUnits() {
        return unitRepository.findByIsOccupiedFalse().stream().map(this::toResponse)
                .toList();
    }

    @Override
    public Unit getUnitById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
    }

    @Override
    public Unit saveUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    public UnitResponse updateUnitStatus(Long id, String status) {
        Unit unit = getUnitById(id);
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        boolean occupied = "OCCUPIED".equalsIgnoreCase(status);

        if (occupied && unit.getTenant() == null) {
            throw new IllegalStateException("Cannot mark unit occupied without assigning a tenant first");
        }

        unit.setIsOccupied(occupied);
        if (!occupied) {
            unit.setTenant(null);
        }
        Unit updatedUnit = unitRepository.save(unit);
        return toResponse(updatedUnit);
    }

    @Override
    public UnitResponse assignTenantToUnit(Long id, AssignTenantRequest request) {
        Unit unit = getUnitById(id);
        if (request == null || request.tenantPhoneNumber() == null || request.tenantPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Tenant phone number is required");
        }

        var tenant = userRepository.findByPhoneNumber(request.tenantPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant.getRole() != Role.TENANT) {
            throw new IllegalStateException("The selected user is not a tenant");
        }

        unit.setTenant(tenant);
        unit.setIsOccupied(true);
        Unit updatedUnit = unitRepository.save(unit);
        return toResponse(updatedUnit);
    }

    private UnitResponse toResponse(Unit unit) {
        String extractedTenantName = null;

        if (unit.getTenant() != null) {
            extractedTenantName = unit.getTenant().fullName();
        }
        return new UnitResponse(
                unit.getId(),
                unit.getUnitNumber(),
                unit.getRentAmount(),
                unit.getFloor(),
                unit.getCreatedAt(),
                unit.getIsOccupied(),
                extractedTenantName
        );
    }
}
