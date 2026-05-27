package com.westoncodeops.zonixrental.service.PaymentService;


import com.westoncodeops.zonixrental.DTOs.Requests.PaymentRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.PaymentResponse;
import com.westoncodeops.zonixrental.entities.Payment;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.enums.PaymentStatus;
import com.westoncodeops.zonixrental.exceptions.ResourceNotFoundException;
import com.westoncodeops.zonixrental.repository.PaymentRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService{

    private final PaymentRepository paymentRepository;
    private final UnitRepository unitRepository;

    @Override
    public PaymentResponse recordPayment(PaymentRequest request) {
        // Check if Unit Exists and Tenant Lives there
        Unit unit = unitRepository.findById(request.unitId()).
                orElseThrow(()-> new RuntimeException("Unit not found"));

        if(!unit.getIsOccupied() || unit.getTenant() == null){
            throw new IllegalStateException("Cannot accept payment for a vacant unit");
        }

        //Calculate the months paid
        BigDecimal rentAmount = unit.getRentAmount();
        int monthsPaid = request.amount().divideToIntegralValue(rentAmount).intValue();

        if (monthsPaid < 1) {
            throw new IllegalArgumentException("Payment amount is less than one month's rent.");
        }

        // Determine start and end dates
        LocalDate startDate = determineStartDate(unit.getId());
        LocalDate endDate = startDate.plusMonths(monthsPaid).minusDays(1);

        // Build and save entity
        Payment payment = Payment.builder()
                .amount(request.amount())
                .mpesaRef(request.mpesaRef())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PAID)
                .coversFrom(startDate)
                .coversUntil(endDate)
                .tenant(unit.getTenant())
                .unit(unit)
                .payer(request.payersPhoneNumber())
                .build();

Payment savedPayment = paymentRepository.save(payment);
return toResponse(savedPayment);

    }

    @Override
    public LocalDate determineStartDate(Long unitId) {
        //Look up latest payment for unit
        return paymentRepository.findTopByUnitIdOrderByCoversUntilDesc(unitId)
                .map(lastpayment -> lastpayment.getCoversUntil().plusDays(1))
                // Start the day after the last payment ends
                .orElseGet(()->LocalDate.now().withDayOfMonth(1));
                // If no history is found, start on the 1st of the current month
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().
                map(this::toResponse).toList();
    }


    private  PaymentResponse toResponse(Payment payment) {

        String tenantName = payment.getTenant().fullName();

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getMpesaRef(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getCoversFrom(),
                payment.getCoversUntil(),
                tenantName,
                payment.getUnit().getUnitNumber()
        );
    }
}
