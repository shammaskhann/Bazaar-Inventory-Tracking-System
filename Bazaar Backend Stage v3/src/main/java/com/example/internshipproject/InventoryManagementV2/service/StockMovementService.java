package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import com.example.internshipproject.InventoryManagementV2.entities.StockMovement;
import com.example.internshipproject.InventoryManagementV2.entities.StockMovementMessage;
import com.example.internshipproject.InventoryManagementV2.repositories.StockMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockMovementService {

    @Autowired
    StockMovementRepository stockMovementRepository;


    public void processStockMovement(StockMovementMessage message) {
        StockMovement entity = new StockMovement();
        entity.setProduct(message.getProduct());
        entity.setStore(message.getStore());
        entity.setQuantity(message.getQuantity());
        entity.setUser(message.getUser());
        entity.setChangeType(message.getChangeType());
        entity.setMovementTime(message.getMovementTime());

        stockMovementRepository.save(entity); // This hits the master DB
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "stockMovement", key = "#storeId.orElse(null) + '-' + #productId.orElse(null) + '-' + #changeType.orElse(null) + '-' + #fromDate.orElse(null) + '-' + #toDate.orElse(null)")
    public List<StockMovement> findStockMovements(Optional<Long> storeId,
                                                  Optional<Long> productId,
                                                  Optional<ChangeType> changeType,
                                                  Optional<LocalDateTime> fromDate,
                                                  Optional<LocalDateTime> toDate) {
        return stockMovementRepository.findStockMovements(
                storeId.orElse(null),
                productId.orElse(null),
                changeType.orElse(null),
                fromDate.orElse(LocalDateTime.now().minusDays(30)),
                toDate.orElse(LocalDateTime.now())
        );
    }
}
