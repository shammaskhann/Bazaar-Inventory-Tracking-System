package com.example.internshipproject.InventoryManagementV2.entities;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class StockMovementMessage implements Serializable {
    private Product product;
    private Store store;
    private int quantity;
    private ChangeType changeType;
    private UserEntity user;
    private LocalDateTime movementTime;
}
