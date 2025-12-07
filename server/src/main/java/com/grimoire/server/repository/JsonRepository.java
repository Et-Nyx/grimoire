package com.grimoire.server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public abstract class JsonRepository<T> {
    
    protected final ObjectMapper objectMapper;
    protected final String DATA_DIR = "data";
    
    public JsonRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        initializeDataDirectory();
    }
    
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR, getSubDirectory()));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize data directory: " + getSubDirectory(), e);
        }
    }
    
    protected abstract String getSubDirectory();
    protected abstract Class<T> getEntityClass();
    
    public void save(T entity, String id) throws IOException {
        File file = Paths.get(DATA_DIR, getSubDirectory(), id + ".json").toFile();
        objectMapper.writeValue(file, entity);
    }
    
    public Optional<T> findById(String id) {
        try {
            File file = Paths.get(DATA_DIR, getSubDirectory(), id + ".json").toFile();
            if (file.exists()) {
                return Optional.of(objectMapper.readValue(file, getEntityClass()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading entity with ID: " + id, e);
        }
        return Optional.empty();
    }
    
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try {
            File dir = Paths.get(DATA_DIR, getSubDirectory()).toFile();
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    entities.add(objectMapper.readValue(file, getEntityClass()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading all entities from: " + getSubDirectory(), e);
        }
        return entities;
    }
    
    public void deleteById(String id) throws IOException {
        Files.deleteIfExists(Paths.get(DATA_DIR, getSubDirectory(), id + ".json"));
    }
}