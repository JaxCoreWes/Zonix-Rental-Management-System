package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateExtensionRequest;
import com.westoncodeops.zonixrental.DTOs.Requests.CreateTicketRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.*;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.entities.Payment;
import com.westoncodeops.zonixrental.enums.MaintenanceCategory;
import com.westoncodeops.zonixrental.enums.PaymentStatus;
import com.westoncodeops.zonixrental.repository.PaymentRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.repository.UserRepository;
import com.westoncodeops.zonixrental.service.ExtensionService.IExtensionService;
import com.westoncodeops.zonixrental.service.MaintenanceService.IMaintenanceService;
import com.westoncodeops.zonixrental.service.PaymentService.IPaymentService;
import com.westoncodeops.zonixrental.service.UserService.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class TenantPortalController {

    private final IUserService userService;
    private final IPaymentService paymentService;
    private final IMaintenanceService maintenanceService;
    private final IExtensionService extensionService;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/tenant-portal")
    public String tenantPortal() {
        return "tenant-portal/index";
    }

    @GetMapping("/tenant-portal/api/tenant/{phone}")
    @ResponseBody
    public Map<String, Object> getTenantData(@PathVariable String phone) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserResponse tenant = userService.getUserByPhone(phone);
            result.put("success", true);
            result.put("tenant", tenant);

            List<Unit> units = unitRepository.findAll();
            Unit tenantUnit = units.stream()
                    .filter(u -> u.getTenant() != null && u.getTenant().getPhoneNumber().equals(phone))
                    .findFirst()
                    .orElse(null);

            if (tenantUnit != null) {
                Map<String, Object> unitData = new HashMap<>();
                unitData.put("id", tenantUnit.getId());
                unitData.put("unitNumber", tenantUnit.getUnitNumber());
                unitData.put("floor", tenantUnit.getFloor());
                unitData.put("rentAmount", tenantUnit.getRentAmount().doubleValue());
                result.put("unit", unitData);

                // Get tenant's tickets using maintenanceService
                List<MaintenanceTicketResponse> tickets = maintenanceService.getTenantTickets(phone);
                result.put("tickets", tickets);

                // Get all payments and filter by tenant
                List<PaymentResponse> allPayments = paymentService.getAllPayments();
                List<PaymentResponse> tenantPayments = allPayments.stream()
                        .filter(p -> {
                            // For now, show all payments since we don't have tenant ID in PaymentResponse
                            return true;
                        })
                        .toList();
                result.put("payments", tenantPayments);

                // Get all extensions
                List<ExtensionResponse> extensions = extensionService.getPendingExtensions();
                result.put("extensions", extensions);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/tenant-portal/api/maintenance")
    @ResponseBody
    public Map<String, Object> createMaintenanceTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = (String) request.get("phoneNumber");
            String description = (String) request.get("description");
            String categoryStr = (String) request.get("category");
            MaintenanceCategory category = MaintenanceCategory.valueOf(categoryStr.toUpperCase());

            CreateTicketRequest ticketRequest = new CreateTicketRequest(phone, category, description);
            MaintenanceTicketResponse response = maintenanceService.createTicket(ticketRequest);
            
            result.put("success", true);
            result.put("ticket", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/tenant-portal/api/extension")
    @ResponseBody
    public Map<String, Object> requestExtension(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = (String) request.get("phoneNumber");
            String reason = (String) request.get("reason");
            String promisedDateStr = (String) request.get("promisedPaymentDate");
            LocalDate promisedDate = LocalDate.parse(promisedDateStr);

            CreateExtensionRequest extensionRequest = new CreateExtensionRequest(phone, promisedDate, reason);
            ExtensionResponse response = extensionService.requestRentPaymentExtension(extensionRequest);
            
            result.put("success", true);
            result.put("extension", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/tenant-portal/api/units")
    @ResponseBody
    public List<Map<String, Object>> getUnits() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Unit> units = unitRepository.findAll();
        for (Unit unit : units) {
            Map<String, Object> unitData = new HashMap<>();
            unitData.put("id", unit.getId());
            unitData.put("unitNumber", unit.getUnitNumber());
            unitData.put("floor", unit.getFloor());
            unitData.put("rentAmount", unit.getRentAmount());
            unitData.put("isOccupied", unit.getIsOccupied());
            if (unit.getTenant() != null) {
                unitData.put("tenantName", unit.getTenant().fullName());
            }
            result.add(unitData);
        }
        return result;
    }

    @PostMapping("/tenant-portal/api/simulate-payment")
    @ResponseBody
    public Map<String, Object> simulatePayment(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = (String) request.get("phoneNumber");
            Number amountNum = (Number) request.get("amount");
            double amount = amountNum.doubleValue();
            Number unitIdNum = (Number) request.get("unitId");
            Long unitId = unitIdNum != null ? unitIdNum.longValue() : null;
            String mpesaRef = (String) request.getOrDefault("mpesaRef", "SIM" + System.currentTimeMillis());

            Optional<User> tenantOpt = userRepository.findByPhoneNumber(phone);
            Optional<Unit> unitOpt = unitId != null ? unitRepository.findById(unitId) : Optional.empty();

            if (tenantOpt.isPresent()) {
                Unit unit = unitOpt.orElseGet(() ->
                    unitRepository.findAll().stream()
                        .filter(u -> u.getTenant() != null && u.getTenant().getPhoneNumber().equals(phone))
                        .findFirst()
                        .orElse(null)
                );

                if (unit != null) {
                    Payment payment = Payment.builder()
                            .amount(BigDecimal.valueOf(amount))
                            .mpesaRef(mpesaRef)
                            .status(PaymentStatus.PAID)
                            .paymentDate(java.time.LocalDateTime.now())
                            .tenant(tenantOpt.get())
                            .unit(unit)
                            .payer(phone)
                            .coversFrom(java.time.LocalDate.now())
                            .coversUntil(java.time.LocalDate.now().plusMonths(1))
                            .build();

                    paymentRepository.save(payment);

                    result.put("success", true);
                    result.put("mpesaRef", mpesaRef);
                } else {
                    result.put("success", false);
                    result.put("message", "Unit not found");
                }
            } else {
                result.put("success", false);
                result.put("message", "Tenant not found");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
