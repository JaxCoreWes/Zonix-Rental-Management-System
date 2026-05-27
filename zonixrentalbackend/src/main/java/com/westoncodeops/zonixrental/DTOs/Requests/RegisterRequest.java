package com.westoncodeops.zonixrental.DTOs.Requests;

import com.westoncodeops.zonixrental.enums.Role;

import java.time.LocalDateTime;

public record RegisterRequest(String firstName,
                              String lastName,
                              String email,
                              String password,
                              LocalDateTime createdAt) {
}
