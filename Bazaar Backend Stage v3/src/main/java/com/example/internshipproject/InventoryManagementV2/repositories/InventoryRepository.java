package com.example.internshipproject.InventoryManagementV2.repositories;

import com.example.internshipproject.InventoryManagementV2.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByStoreIdAndProductId(Long storeId, Long productId);

    @Query(nativeQuery = true, value = """
    SELECT id, product_id, store_id, quantity, last_updated 
    FROM inventory 
    WHERE store_id = ?1
""")
    List<Inventory> findByStoreId(Long storeId);
}
