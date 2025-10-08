package com.example.internshipproject.InventoryManagementV2.repositories;

import com.example.internshipproject.InventoryManagementV2.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsernameAndPassword(String username, String password);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
