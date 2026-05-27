package com.westoncodeops.zonixrental.DTOs.Responses.Caretaker;

public record CaretakerUnitView(Long unitId,
                                String unitNumber,
                                String tenantName,
                                String tenantPhone,
                                String rentStatus) {

    // rentStatus: Will output: "FULLY PAID", "PARTIAL (Owes: X)", "UNPAID", or "VACANT"
}
