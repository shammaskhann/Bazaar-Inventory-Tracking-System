package com.example.internshipproject.InventoryManagementV2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private Environment environment;  // Inject Environment

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        log.info("Health check endpoint accessed");
        log.info(environment.getProperty("server.port"));

        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Service is running",
                "port", environment.getProperty("server.port")  // Get the port from environment properties
        ));
    }
}
