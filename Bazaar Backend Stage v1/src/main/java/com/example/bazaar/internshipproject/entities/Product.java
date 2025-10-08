package com.example.bazaar.internshipproject.entities;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {


    private Long id;

    private String name;

    private String description;

    private Double price;

    private LocalDateTime createdAt = LocalDateTime.now();
}