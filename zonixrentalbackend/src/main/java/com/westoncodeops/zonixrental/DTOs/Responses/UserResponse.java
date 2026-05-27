package com.westoncodeops.zonixrental.DTOs.Responses;

import com.westoncodeops.zonixrental.enums.Role;

import java.util.UUID;

public record UserResponse(UUID id,
                           String fullname,
                           String phoneNumber,
                           Role role) {
}
