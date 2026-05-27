package com.westoncodeops.zonixrental.controllers;

import com.westoncodeops.zonixrental.repository.UserRepository;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.DTOs.Responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            // 1. Check if keys exist to prevent NullPointerExceptions
            if (credentials == null || !credentials.containsKey("identifier") || !credentials.containsKey("password")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Missing required fields. Frontend must send 'identifier' and 'password'."
                ));
            }

            String identifier = credentials.get("identifier");
            String password = credentials.get("password");

            System.out.println("Checking login for identifier: " + identifier);

            // 2. Query database
            Optional<User> userOpt = userRepository.findByEmailOrPhoneNumber(identifier, identifier);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Safety check if password in DB is null
                if (user.getPassword() == null) {
                    return ResponseEntity.status(500).body(Map.of("error", "User exists but has no password set in database."));
                }

                if (user.getPassword().equals(password)) {
                    // Match your exact custom UserResponse structure
                    UserResponse responseData = new UserResponse(
                            user.getId(),
                            user.fullName(),
                            user.getPhoneNumber(),
                            user.getRole()
                    );

                    return ResponseEntity.ok(Map.of(
                            "user", responseData,
                            "authenticated", true
                    ));
                }
            }

            return ResponseEntity.status(401).body(Map.of("authenticated", false, "message", "Invalid credentials."));

        } catch (Exception e) {
            // This prints the exact error stack trace to your Spring Boot terminal console
            System.err.println("❌ CRITICAL LOGIN CRASH DETECTED:");
            e.printStackTrace();

            // Sends the exact message back to JavaFX so you can read it instantly
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Backend crashed: " + e.getMessage()
            ));
        }
    }
}