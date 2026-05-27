package com.westoncodeops.zonixrental.repository;

import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);
    long countByRole(Role role);
    java.util.List<User> findAllByRole(Role role);

}
