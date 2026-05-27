package com.westoncodeops.zonixrental.service.ExtensionService;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateExtensionRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.ExtensionResponse;
import com.westoncodeops.zonixrental.entities.ExtensionRequest;
import com.westoncodeops.zonixrental.enums.ExtensionStatus;

import java.util.List;
import java.util.UUID;

public interface IExtensionService {

    ExtensionResponse requestRentPaymentExtension(CreateExtensionRequest request);
    ExtensionResponse reviewExtension(UUID extensionId, ExtensionStatus status, String caretakerPhone);//By Caretaker // Reviewed By Landlord
    List<ExtensionResponse> getPendingExtensions();
}
