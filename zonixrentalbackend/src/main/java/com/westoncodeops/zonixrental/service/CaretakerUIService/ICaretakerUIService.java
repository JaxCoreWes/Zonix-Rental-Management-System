package com.westoncodeops.zonixrental.service.CaretakerUIService;

import com.westoncodeops.zonixrental.DTOs.Responses.Caretaker.CaretakerUnitView;
import com.westoncodeops.zonixrental.entities.Unit;

import java.time.LocalDateTime;
import java.util.List;

public interface ICaretakerUIService {

    List<CaretakerUnitView> getCollectionStatus();

}
