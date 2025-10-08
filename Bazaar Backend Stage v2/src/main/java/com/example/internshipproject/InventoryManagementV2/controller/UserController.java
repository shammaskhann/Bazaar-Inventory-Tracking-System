package com.example.internshipproject.InventoryManagementV2.controller;

import com.example.internshipproject.InventoryManagementV2.entities.Store;
import com.example.internshipproject.InventoryManagementV2.entities.UserCredentials;
import com.example.internshipproject.InventoryManagementV2.entities.UserEntity;
import com.example.internshipproject.InventoryManagementV2.service.StoreService;
import com.example.internshipproject.InventoryManagementV2.service.UserService;
import com.example.internshipproject.InventoryManagementV2.utils.JwtUtil;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            Integer storeId = (Integer) request.get("store_id");

            if (username == null || password == null || storeId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Username, password, and store_id are required"
                ));
            }

            if (userService.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Username already exists"
                ));
            }

            Store store = storeService.findStoreById(storeId.longValue());
            if(store == null){
                throw new RuntimeException("Store not found");
            }

            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(password);
            user.setStoreId(storeId.longValue());
            userService.register(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/login")
    @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> loginPost(@RequestBody UserCredentials userCredentials) {
        if(userCredentials.getUsername() == null || userCredentials.getPassword() == null) return new ResponseEntity<>(
                Map.of("status", false, "message", "Email or password is required")
                , HttpStatus.BAD_REQUEST);
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), userCredentials.getPassword()));
            final UserEntity user  = userService.findByUsername(userCredentials.getUsername());
            String jwtToken = jwtUtil.generateToken(user.getUsername());

            if(user == null){
                return new ResponseEntity<>(
                        Map.of("status", false,"message","User not found" )
                        ,HttpStatus.NOT_FOUND);
            }else{
                return new ResponseEntity<>(
                        Map.of("message","User Successfully Login", "status", true,  "data", Map.of(
                                "token", jwtToken,
                                "user", user
                        )),
                        HttpStatus.OK);
            }
        }
        catch (AuthenticationException e) {

            log.error("Exception occurred while createAuthenticationToken", e);
            return new ResponseEntity<>(
                    Map.of(
                            "status", false,
                            "message", "Invalid email or password"
                    ),
                    HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> rateLimitFallback(UserCredentials credentials, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message","Too many requests - please try again later."));
    }

}
