package com.example.internshipproject.InventoryManagementV2.repositories;

import com.example.internshipproject.InventoryManagementV2.entities.Store;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findStoreById(Long id);


}
