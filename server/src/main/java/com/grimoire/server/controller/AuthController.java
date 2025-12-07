package com.grimoire.server.controller;

import com.grimoire.common.model.User;
import com.grimoire.server.service.JsonPersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JsonPersistenceService persistenceService;

    public AuthController(JsonPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> existingUser = persistenceService.findUserByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).build(); // Conflict
        }

        try {
            // Ensure ID is generated
            if (user.getId() == null) {
                user = User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build();
            }
            // TODO: Implement password hashing before saving (e.g., BCrypt)
            persistenceService.saveUser(user);
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = persistenceService.findUserByUsername(loginRequest.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // TODO: Implement password verification with hash comparison (e.g., BCrypt.checkpw())
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }
        
        return ResponseEntity.status(401).build(); // Unauthorized
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return persistenceService.loadUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        if (!id.equals(user.getId().toString())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            persistenceService.saveUser(user);
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            persistenceService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
