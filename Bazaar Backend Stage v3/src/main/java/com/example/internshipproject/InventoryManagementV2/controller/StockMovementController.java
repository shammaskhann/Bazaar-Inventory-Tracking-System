package com.example.internshipproject.InventoryManagementV2.controller;

import com.example.internshipproject.InventoryManagementV2.core.domain.ChangeType;
import com.example.internshipproject.InventoryManagementV2.entities.*;
import com.example.internshipproject.InventoryManagementV2.repositories.InventoryRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.ProductRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.StockMovementRepository;
import com.example.internshipproject.InventoryManagementV2.repositories.StoreRepository;
import com.example.internshipproject.InventoryManagementV2.service.*;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockMovementController {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final StockMovementService stockMovementService;

    private final StockMovementProducer stockMovementProducer;


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
            Store store = storeService.findStoreById(storeId)
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            Optional<Product> productOpt = productService.findProductById(request.getProductId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", false, "message", "Product not found"));
            }


            Inventory inventory = inventoryService.findByStoreIdAndProductId(storeId, request.getProductId())
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

            inventoryService.saveProductInventory(inventory);


            StockMovementMessage stockMovementMessage = new StockMovementMessage();
            stockMovementMessage.setProduct(productOpt.get());
            stockMovementMessage.setStore(store);
            stockMovementMessage.setUser(user);
            stockMovementMessage.setQuantity(Math.abs(request.getQuantity()));
            stockMovementMessage.setChangeType(request.getChangeType());
            stockMovementMessage.setMovementTime(LocalDateTime.now());

            stockMovementProducer.sendStockMovement(stockMovementMessage);


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
                @RequestParam Optional<Long> productId,
                @RequestParam Optional<ChangeType> changeType,
                @RequestParam Optional<LocalDateTime> fromDate,
                @RequestParam Optional<LocalDateTime> toDate) {

            try{
                log.info("Get stock movement list");
                List<StockMovement> movements = stockMovementService.findStockMovements(
                        storeId,
                        productId,
                        changeType,
                        fromDate,
                        toDate
                );

                return ResponseEntity.ok(movements);
            }catch (Exception e){
                log.error("Get stock movement list error", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status", false, "message", e.getMessage()));
            }
    }



    public ResponseEntity<?> rateLimitFallback(UserCredentials credentials, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message","Too many requests - please try again later."));
    }
}

