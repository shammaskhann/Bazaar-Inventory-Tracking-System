package com.example.bazaar.internshipproject.controller;

import com.example.bazaar.internshipproject.entities.Product;
import com.example.bazaar.internshipproject.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try{
            Product newProduct = productRepository.addProduct(product);
            return new ResponseEntity<>(
                    Map.of("status", true, "data", newProduct),
                    HttpStatus.CREATED
            );
        }catch (Exception e){
            return new ResponseEntity<>(
                    Map.of("status", false, "message", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    public ResponseEntity<?> getProducts() {
        try{
            List<Product> products = productRepository.getAllProduct();
            System.out.println(products);
            return new ResponseEntity<>(
                    Map.of(
                            "status", true,
                            "data", products.toArray()
                    ),
                    HttpStatus.OK
            );
        }catch (Exception e){
            return new ResponseEntity<>(
                    Map.of(
                            "status", false,
                            "message", e.getMessage()
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestParam Long id) {
        try {
            boolean deleted = productRepository.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("status", true, "message", "Product deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", false, "message", "Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", false, "message", e.getMessage()));
        }
    }
}
