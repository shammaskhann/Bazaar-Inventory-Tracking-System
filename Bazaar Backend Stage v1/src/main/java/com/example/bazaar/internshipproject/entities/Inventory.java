package com.example.bazaar.internshipproject.entities;

import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Inventory {

    private Long id;

    private Product product;

    private int quantity;

    private LocalDateTime updatedAt = LocalDateTime.now();
}