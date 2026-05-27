package com.westoncodeops.zonixrental.DTOs.Requests;

import com.westoncodeops.zonixrental.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;

public record UserRequest(@NotNull String firstName,
                          @NotNull String lastName,
                          @NotNull String phoneNumber,
                          @Nullable String email,
                          @Nullable String password,
                          @NotNull Role role,
                          LocalDateTime createdAt) {
}
