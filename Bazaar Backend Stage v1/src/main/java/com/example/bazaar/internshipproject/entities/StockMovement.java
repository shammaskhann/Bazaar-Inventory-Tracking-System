package com.example.bazaar.internshipproject.entities;

import com.example.bazaar.internshipproject.core.domain.StockType;
import lombok.*;
import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StockMovement {

    private Long id;

    private Product product;

    private StockType type;

    private int quantity;

    private LocalDateTime timestamp = LocalDateTime.now();
}
