package com.example.internshipproject.InventoryManagementV2.entities;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
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
@Table(name="stock_movements")
@Access(AccessType.FIELD)
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

//    private Long user_id;

    @Column(name = "movement_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime movementTime = LocalDateTime.now();
}
