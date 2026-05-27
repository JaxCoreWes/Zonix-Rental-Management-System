package com.westoncodeops.zonixrental.controllers;


import com.westoncodeops.zonixrental.DTOs.Requests.PaymentRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.PaymentResponse;
import com.westoncodeops.zonixrental.service.PaymentService.IPaymentService;
import com.westoncodeops.zonixrental.service.PaymentService.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

private final IPaymentService  paymentService;

    @PostMapping("/record")
    public ResponseEntity<PaymentResponse> recordPayment(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.recordPayment(request), HttpStatus.CREATED);
    }


    // Landlord/Full History View
    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }



}
