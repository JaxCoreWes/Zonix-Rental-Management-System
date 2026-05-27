package com.westoncodeops.zonixrental.service.UnitService;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateUnitRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UnitResponse;
import com.westoncodeops.zonixrental.entities.Unit;

import java.util.List;

public interface IUnitService {

UnitResponse createUnit(CreateUnitRequest request);
List<UnitResponse> getAllUnits();
List<UnitResponse> getVacantUnits();
Unit getUnitById(Long id);
Unit saveUnit(Unit unit);


}
