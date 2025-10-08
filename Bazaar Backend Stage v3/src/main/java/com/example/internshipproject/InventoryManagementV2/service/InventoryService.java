package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.entities.Inventory;
import com.example.internshipproject.InventoryManagementV2.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;


    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryCache", key = "#storeId", unless = "#result == null")
    public List<Inventory> getAllStocks(Long storeId) {
        return inventoryRepository.findByStoreId(storeId);
    }

    public void saveProductInventory(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryCache", key = "#storeId  + '-' + #productId", unless = "#result == null")
    public Optional<Inventory> findByStoreIdAndProductId(Long storeId, Long productId){
        return inventoryRepository.findByStoreIdAndProductId(storeId, productId);
    }


}
