package com.example.internshipproject.InventoryManagementV2.entities;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockMovementRequest {
    private Long productId;
    private Integer quantity;
    private ChangeType changeType;
}

