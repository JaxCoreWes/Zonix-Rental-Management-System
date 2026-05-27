package com.westoncodeops.zonixrental.service.PaymentService;

import com.westoncodeops.zonixrental.DTOs.Requests.PaymentRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.PaymentResponse;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {

    PaymentResponse recordPayment(PaymentRequest request);
    LocalDate determineStartDate(Long id);
    List<PaymentResponse> getAllPayments();

}
