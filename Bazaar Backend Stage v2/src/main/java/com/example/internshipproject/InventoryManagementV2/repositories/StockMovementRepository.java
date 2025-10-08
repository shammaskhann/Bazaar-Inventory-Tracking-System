package com.example.internshipproject.InventoryManagementV2.repositories;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import com.example.internshipproject.InventoryManagementV2.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {


    @Query("SELECT sm FROM StockMovement sm " +
            "WHERE (:storeId IS NULL OR sm.store.id = :storeId) " +
            "AND (:userId IS NULL OR sm.user_id = :userId) " +
            "AND (:productId IS NULL OR sm.product.id = :productId) " +
            "AND (:changeType IS NULL OR sm.changeType = :changeType) " +
            "AND sm.movementTime BETWEEN :fromDate AND :toDate")
    List<StockMovement> findStockMovements(
            @Param("storeId") Long storeId,
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("changeType") ChangeType changeType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
