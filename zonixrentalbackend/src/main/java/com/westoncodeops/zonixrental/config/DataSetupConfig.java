package com.westoncodeops.zonixrental.config;

import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.Role;
import com.westoncodeops.zonixrental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class DataSetupConfig {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (repository.findByEmail("landlord@zonix.com").isEmpty()) {
                User landlord = User.builder()
                        .firstName("Master")
                        .lastName("Landlord")
                        .email("landlord@zonix.com")
                        .phoneNumber("0700000000")
                        .password("landlord123") // Plain text for your local testing today
                        .role(Role.LANDLORD)
                        .createdAt(LocalDateTime.now())
                        .build();
                repository.save(landlord);
                System.out.println("🚀 Test Landlord Seeded: landlord@zonix.com / landlord123");
            }
        };
    }
}
