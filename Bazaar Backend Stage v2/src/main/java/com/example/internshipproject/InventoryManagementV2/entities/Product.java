package com.example.internshipproject.InventoryManagementV2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
