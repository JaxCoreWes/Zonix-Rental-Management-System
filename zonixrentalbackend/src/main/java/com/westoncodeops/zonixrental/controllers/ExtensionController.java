package com.westoncodeops.zonixrental.controllers;


import com.westoncodeops.zonixrental.DTOs.Requests.CreateExtensionRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.ExtensionResponse;
import com.westoncodeops.zonixrental.entities.ExtensionRequest;
import com.westoncodeops.zonixrental.enums.ExtensionStatus;
import com.westoncodeops.zonixrental.service.ExtensionService.IExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequestMapping("/api/v1/extensions")
@RestController
@RequiredArgsConstructor
public class ExtensionController {

    private final IExtensionService extensionService;

    @PostMapping("/request")
    public ResponseEntity<ExtensionResponse> requestExtension(@Valid @RequestBody CreateExtensionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(extensionService.requestRentPaymentExtension(request));
    }

    @GetMapping
    public ResponseEntity<List<ExtensionResponse>> getAllExtensions(){
        return ResponseEntity.status(HttpStatus.OK).body(extensionService.getPendingExtensions());
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<ExtensionResponse> reviewRequest(
            @PathVariable UUID id,
            @RequestBody Map<String, String> actionRequest){
        String action = actionRequest.getOrDefault("action", "").toUpperCase();
        String caretakerPhone = actionRequest.getOrDefault("caretakerPhone", "");
        ExtensionStatus status = "APPROVE".equals(action) ? ExtensionStatus.APPROVED : ExtensionStatus.REJECTED;
        return ResponseEntity.ok(extensionService.reviewExtension(id, status, caretakerPhone));
    }


}
