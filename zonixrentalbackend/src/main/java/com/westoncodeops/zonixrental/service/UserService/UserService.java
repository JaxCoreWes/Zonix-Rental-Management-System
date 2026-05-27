package com.westoncodeops.zonixrental.service.UserService;


import com.westoncodeops.zonixrental.DTOs.Requests.UserRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UserResponse;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.Role;
import com.westoncodeops.zonixrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository  userRepository;


    @Override
    public User getEntityByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id).
                orElseThrow(()->new RuntimeException("User not found"));

    return toResponse(user);
    }

    @Override
    public UserResponse saveUser(UserRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(request.password())
                .role(request.role())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public UserResponse getUserByPhone(String phone) {
        // 1. Get the raw entity
        User user = getEntityByPhone(phone);

        // 2. Map it and return the clean DTO
        return new UserResponse(
                user.getId(),
                user.fullName(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

    @Override
    public java.util.List<UserResponse> getCaretakers() {
        return userRepository.findAllByRole(Role.CARETAKER).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public java.util.List<UserResponse> getTenants() {
        return userRepository.findAllByRole(Role.TENANT).stream()
                .map(this::toResponse)
                .toList();
    }


    private UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.fullName(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
