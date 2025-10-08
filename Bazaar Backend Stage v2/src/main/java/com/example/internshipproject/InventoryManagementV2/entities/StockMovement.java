package com.example.internshipproject.InventoryManagementV2.entities;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    private Integer quantity;

    private Long user_id;

    @Column(name = "movement_time")
    private LocalDateTime movementTime = LocalDateTime.now();
}
