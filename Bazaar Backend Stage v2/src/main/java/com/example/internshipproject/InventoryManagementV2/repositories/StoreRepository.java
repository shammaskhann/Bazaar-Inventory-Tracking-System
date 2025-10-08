package com.example.internshipproject.InventoryManagementV2.repositories;

import com.example.internshipproject.InventoryManagementV2.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
