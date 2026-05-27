package com.westoncodeops.zonixrental.service.ExtensionService;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateExtensionRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.ExtensionResponse;
import com.westoncodeops.zonixrental.entities.ExtensionRequest;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.ExtensionStatus;
import com.westoncodeops.zonixrental.repository.ExtensionRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.service.UserService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExtensionService implements IExtensionService{

    private final ExtensionRepository extensionRepository;
    private final UserService userService;
    private final UnitRepository unitRepository;

    @Override
    public ExtensionResponse requestRentPaymentExtension(CreateExtensionRequest request) {
        User tenant = userService.getEntityByPhone(request.phoneNumber());

        Unit unit = unitRepository.findByTenantId(tenant.getId()).
                orElseThrow(()-> new IllegalStateException("Tenant is not assigned a unit"));

        ExtensionRequest extension = ExtensionRequest.builder()
                .tenant(tenant)
                .unit(unit)
                .expectedPaymentDate(request.expectedPaymentDate())
                .reason(request.reason())
                .status(ExtensionStatus.PENDING)
                .reviewedBy(null)
                .createdAt(LocalDateTime.now())
                .build();

        ExtensionRequest savedExtension = extensionRepository.save(extension);
        return  toResponse(savedExtension);

    }

    @Override
    @Transactional
    public ExtensionResponse reviewExtension(UUID extensionId, ExtensionStatus newStatus, String caretakerPhone) {
        ExtensionRequest extension = extensionRepository.findById(extensionId)
                .orElseThrow(() -> new RuntimeException("Extension request not found"));

        if (extension.getStatus() != ExtensionStatus.PENDING) {
            throw new IllegalStateException("This request has already been " + extension.getStatus());
        }

        User caretaker = userService.getEntityByPhone(caretakerPhone);

        extension.setStatus(newStatus);
        extension.setReviewedBy(caretaker);

        return toResponse(extensionRepository.save(extension));
    }

    @Override
    public List<ExtensionResponse> getPendingExtensions() {
        return extensionRepository.findAll().stream().map(this::toResponse)
                .toList();
    }



    private ExtensionResponse toResponse(ExtensionRequest ext) {

        String tenantName = ext.getTenant().fullName();
        String reviewerName = (ext.getReviewedBy() != null) ? ext.getReviewedBy().fullName() : "Pending review";
        return new ExtensionResponse(
                ext.getId(),
                tenantName,
                ext.getUnit().getUnitNumber(),
                ext.getExpectedPaymentDate(),
                ext.getReason(),
                ext.getStatus(),
                reviewerName,
                ext.getCreatedAt()
        );
    }

}
