package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.entities.UserCredentials;
import com.example.internshipproject.InventoryManagementV2.entities.UserEntity;

import com.example.internshipproject.InventoryManagementV2.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    UserRepository userRepository;


    public void register(UserEntity userEntity) {
        boolean exist = userRepository.existsByUsername(userEntity.getUsername());
        if (exist) {
            throw new RuntimeException("Username already exists");
        }
        log.info("Registering user: {}", userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        log.info("Password encoded: {}", userEntity.getPassword());
        userRepository.save(userEntity);
    }


//    @Transactional(readOnly = true)
    public UserEntity login(UserCredentials userCredentials) {
        // Encode the password before querying the database
        log.info("Logging in user: {}", userCredentials);
        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        log.info("Password encoded: {}", userCredentials.getPassword());
      return userRepository.findByUsernameAndPassword(userCredentials.getUsername(), userCredentials.getPassword());
    }

//    @Transactional(readOnly = true)
    public UserEntity findByUsername(String username) {
        log.info("Looking up user: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }
}
