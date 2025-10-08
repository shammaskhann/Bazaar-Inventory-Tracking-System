package com.example.internshipproject.InventoryManagementV2.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "inventory")
@Access(AccessType.FIELD)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    @Column(name = "last_updated")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}