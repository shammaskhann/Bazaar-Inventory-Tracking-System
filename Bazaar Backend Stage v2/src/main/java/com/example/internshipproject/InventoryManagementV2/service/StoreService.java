package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.entities.Store;
import com.example.internshipproject.InventoryManagementV2.repositories.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    StoreRepository storeRepository;

    public Store findStoreById(Long Id){
        Store store = storeRepository.findById(Id).orElse(null);
        return store;
    }

}
