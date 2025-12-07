package com.grimoire.server.repository;

import com.grimoire.common.model.User;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository extends JsonRepository<User> {
    
    @Override
    protected String getSubDirectory() {
        return "users";
    }
    
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
    
    public void save(User user) throws IOException {
        // TODO: Implement password hashing before saving
        super.save(user, user.getId().toString());
    }
    
    public Optional<User> findByUsername(String username) {
        try {
            File userDir = Paths.get(DATA_DIR, getSubDirectory()).toFile();
            File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    User user = objectMapper.readValue(file, User.class);
                    if (user.getUsername().equals(username)) {
                        return Optional.of(user);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error searching user by username: " + username, e);
        }
        return Optional.empty();
    }
    
    public Optional<User> findById(UUID id) {
        return super.findById(id.toString());
    }
}