package com.example.internshipproject.InventoryManagementV2.controller;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import com.example.internshipproject.InventoryManagementV2.entities.*;
import com.example.internshipproject.InventoryManagementV2.repositories.InventoryRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.ProductRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.StockMovementRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.StoreRepository;
import com.example.internshipproject.InventoryManagementV2.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserService userService;


    @PostMapping("/StockIn")
    @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> stockIn(@RequestBody StockMovementRequest stockMovement, @AuthenticationPrincipal UserDetails userDetails) {
        stockMovement.setChangeType(ChangeType.STOCK_IN);
        return moveStock(stockMovement, userDetails);
    }

    @PostMapping("/ManualRemoval")
    @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> manualRemoval(@RequestBody StockMovementRequest stockMovement, @AuthenticationPrincipal UserDetails userDetails) {
        stockMovement.setChangeType(ChangeType.MANUAL_REMOVAL);
        return moveStock(stockMovement, userDetails);
    }

    @PostMapping("/Sale")
    @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> sale(@RequestBody StockMovementRequest stockMovement, @AuthenticationPrincipal UserDetails userDetails) {
        stockMovement.setChangeType(ChangeType.SALE);
        return moveStock(stockMovement, userDetails);
    }


    public ResponseEntity<?> moveStock(@RequestBody StockMovementRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {

            UserEntity user = userService.findByUsername(userDetails.getUsername());
            Long storeId = user.getStoreId();
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            Optional<Product> productOpt = productRepository.findById(request.getProductId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", false, "message", "Product not found"));
            }


            Inventory inventory = inventoryRepository.findByStoreIdAndProductId(storeId, request.getProductId())
                    .orElseGet(() -> {

                        Inventory newInventory = new Inventory();
                        newInventory.setStore(store);
                        newInventory.setProduct(productOpt.get());
                        newInventory.setQuantity(0);
                        return newInventory;
                    });


            int quantityChange = request.getChangeType() == ChangeType.STOCK_IN
                    ? request.getQuantity()
                    : -request.getQuantity();

            int updatedQuantity = inventory.getQuantity() + quantityChange;

            if (updatedQuantity < 0) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Insufficient stock"));
            }

            inventory.setQuantity(updatedQuantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);


            StockMovement movement = new StockMovement();
            movement.setProduct(productOpt.get());
            movement.setStore(store);
            movement.setUser_id(user.getId());
            movement.setQuantity(Math.abs(request.getQuantity()));
            movement.setChangeType(request.getChangeType());
            movement.setMovementTime(LocalDateTime.now());
            stockMovementRepository.save(movement);

            return ResponseEntity.ok(Map.of("status", true, "new_quantity", updatedQuantity, "inventory", inventory));
        } catch (AuthenticationCredentialsNotFoundException  e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", false, "message", e));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", false, "message", e.getMessage()));
        }
    }


        @GetMapping("/report")
        @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
        public ResponseEntity<?> getStockMovements(
                @RequestParam Optional<Long> storeId,
                @RequestParam Optional<Long> userId,
                @RequestParam Optional<Long> productId,
                @RequestParam Optional<ChangeType> changeType,
                @RequestParam Optional<LocalDateTime> fromDate,
                @RequestParam Optional<LocalDateTime> toDate) {

            List<StockMovement> movements = stockMovementRepository.findStockMovements(
                    storeId.orElse(null),
                    userId.orElse(null),
                    productId.orElse(null),
                    changeType.orElse(null),
                    fromDate.orElse(LocalDateTime.now().minusDays(30)),
                    toDate.orElse(LocalDateTime.now())
            );

            return ResponseEntity.ok(movements);
    }



    public ResponseEntity<?> rateLimitFallback(UserCredentials credentials, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message","Too many requests - please try again later."));
    }
}

