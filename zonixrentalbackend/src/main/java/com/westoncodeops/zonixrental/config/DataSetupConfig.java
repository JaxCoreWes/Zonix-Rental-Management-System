package com.westoncodeops.zonixrental.config;

import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.enums.Role;
import com.westoncodeops.zonixrental.repository.UserRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSetupConfig {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, UnitRepository unitRepository) {
        return args -> {
            // Seed Landlord
            if (userRepository.findByEmail("landlord@zonix.com").isEmpty()) {
                User landlord = User.builder()
                        .firstName("Master")
                        .lastName("Landlord")
                        .email("landlord@zonix.com")
                        .phoneNumber("0700000000")
                        .password("landlord123")
                        .role(Role.LANDLORD)
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepository.save(landlord);
                System.out.println("🚀 Test Landlord Seeded: landlord@zonix.com / landlord123");
            }

            // Seed Caretaker
            if (userRepository.findByEmail("caretaker@zonix.com").isEmpty()) {
                User caretaker = User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("caretaker@zonix.com")
                        .phoneNumber("0711111111")
                        .password("caretaker123")
                        .role(Role.CARETAKER)
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepository.save(caretaker);
                System.out.println("🚀 Test Caretaker Seeded: caretaker@zonix.com / caretaker123");
            }

            // Seed Sample Tenant
            User sampleTenant = null;
            if (userRepository.findByPhoneNumber("0722222222").isEmpty()) {
                sampleTenant = User.builder()
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane@example.com")
                        .phoneNumber("+254757879071")
                        .password("tenant123")
                        .role(Role.TENANT)
                        .createdAt(LocalDateTime.now())
                        .build();
                sampleTenant = userRepository.save(sampleTenant);
                System.out.println("🚀 Test Tenant Seeded: Phone - 0722222222");
            } else {
                sampleTenant = userRepository.findByPhoneNumber("0722222222").orElse(null);
            }

            // Seed Units
            if (unitRepository.count() == 0) {
                Unit unit1 = Unit.builder()
                        .unitNumber("A101")
                        .floor(1)
                        .rentAmount(new BigDecimal("25000"))
                        .isOccupied(true)
                        .tenant(sampleTenant)
                        .createdAt(LocalDateTime.now())
                        .build();
                unitRepository.save(unit1);

                Unit unit2 = Unit.builder()
                        .unitNumber("A201")
                        .floor(2)
                        .rentAmount(new BigDecimal("35000"))
                        .isOccupied(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                unitRepository.save(unit2);

                Unit unit3 = Unit.builder()
                        .unitNumber("B301")
                        .floor(3)
                        .rentAmount(new BigDecimal("18000"))
                        .isOccupied(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                unitRepository.save(unit3);

                System.out.println("🚀 Test Units Seeded");
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("📱 TENANT PORTAL: http://localhost:8080/tenant-portal");
            System.out.println("🔑 TEST TENANT PHONE: 0722222222");
            System.out.println("=".repeat(60) + "\n");
        };
    }
}
