package com.westoncodeops.zonixrental.repository;

import com.westoncodeops.zonixrental.entities.ExtensionRequest;
import com.westoncodeops.zonixrental.enums.ExtensionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExtensionRepository extends JpaRepository<ExtensionRequest, UUID> {
    long countByStatus(ExtensionStatus status);

}
