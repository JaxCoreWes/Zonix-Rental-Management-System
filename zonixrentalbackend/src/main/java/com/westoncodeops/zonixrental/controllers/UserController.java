package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.DTOs.Requests.UserRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UserResponse;
import com.westoncodeops.zonixrental.service.UserService.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        // This is correct, assuming saveUser() returns a UserResponse DTO
        return new ResponseEntity<>(userService.saveUser(request), HttpStatus.OK);
    }

    @GetMapping("/{phone}")
    public  ResponseEntity<UserResponse> getUserByPhone(@PathVariable String phone){
        return ResponseEntity.ok(userService.getUserByPhone(phone));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getTenants() {
        return ResponseEntity.ok(userService.getTenants());
    }

    @GetMapping("/caretakers")
    public ResponseEntity<List<UserResponse>> getCaretakers() {
        return ResponseEntity.ok(userService.getCaretakers());
    }

}
