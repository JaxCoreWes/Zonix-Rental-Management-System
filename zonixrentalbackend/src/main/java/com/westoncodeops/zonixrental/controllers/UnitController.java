package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateUnitRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UnitResponse;
import com.westoncodeops.zonixrental.service.UnitService.IUnitService;
import com.westoncodeops.zonixrental.service.UnitService.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {

    private final IUnitService unitService;

    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody CreateUnitRequest request) {
        return new ResponseEntity<>(unitService.createUnit(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        List<UnitResponse> units = unitService.getAllUnits();
        return ResponseEntity.ok(units == null ? List.of() : units);
    }
}
