package com.westoncodeops.zonixrental.service.UnitService;


import com.westoncodeops.zonixrental.DTOs.Requests.CreateUnitRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UnitResponse;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.exceptions.ResourceNotFoundException;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {
    private final UnitRepository unitRepository;


    @Override
    public UnitResponse createUnit(CreateUnitRequest request) {
 if(unitRepository.findByUnitNumber(request.unitNumber()).isPresent()) {
     throw new RuntimeException("Unit already exists");
 }

        Unit newUnit = Unit.builder()
                .unitNumber(request.unitNumber())
                .rentAmount(request.rentAmount())
                .Floor(request.Floor())
                .isOccupied(false)
                .tenant(null)
                .createdAt(LocalDateTime.now())
                .build();

        Unit SavedUnit = unitRepository.save(newUnit);
        return toResponse(SavedUnit);

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
        return unitRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Unit not found"));
    }

    @Override
    public Unit saveUnit(Unit unit) {
        return unitRepository.save(unit);
    }


    private UnitResponse toResponse(Unit unit) {

        String extractedTenantName = null;

        if(unit.getTenant() != null) {
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
