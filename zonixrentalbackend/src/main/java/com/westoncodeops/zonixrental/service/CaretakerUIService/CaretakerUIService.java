package com.westoncodeops.zonixrental.service.CaretakerUIService;


import com.westoncodeops.zonixrental.DTOs.Responses.Caretaker.CaretakerUnitView;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.repository.PaymentRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaretakerUIService implements ICaretakerUIService {
   private final UnitRepository unitRepository;
   private final PaymentRepository paymentRepository;

    @Override
    public List<CaretakerUnitView> getCollectionStatus() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth =  currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

    return unitRepository.findAll().stream().map(unit -> buildCaretakerView(unit, startOfMonth, endOfMonth))
            .toList();
    }


    private CaretakerUnitView buildCaretakerView(Unit unit, LocalDateTime start, LocalDateTime end) {
        // 1. Handle Vacant Units
        if (!unit.getIsOccupied() || unit.getTenant() == null) {
            return new CaretakerUnitView(unit.getId(), unit.getUnitNumber(), "N/A", "N/A", "VACANT");
        }

        // 2. Calculate Paid vs Expected
        BigDecimal totalPaidThisMonth = paymentRepository.sumCompletedPaymentsForUnitInDateRange(unit.getId(), start, end);
        BigDecimal expectedRent = unit.getRentAmount();

        // 3. Determine the Status String
        String status;
        if (totalPaidThisMonth.compareTo(expectedRent) >= 0) {
            status = "FULLY PAID";
        } else if (totalPaidThisMonth.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remaining = expectedRent.subtract(totalPaidThisMonth);
            status = "PARTIAL (Owes: " + remaining + ")";
        } else {
            status = "UNPAID";
        }

        // 4. Return the safe DTO
        return new CaretakerUnitView(
                unit.getId(),
                unit.getUnitNumber(),
                unit.getTenant().getFirstName() + " " + unit.getTenant().getLastName(),
                unit.getTenant().getPhoneNumber(),
                status
        );
    }

}
