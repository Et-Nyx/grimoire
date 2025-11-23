package com.grimoire.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grimoire.common.model.CampaignNote;
import com.grimoire.common.model.CharacterSheet;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class JsonPersistenceService {

    private final ObjectMapper objectMapper;
    private final String DATA_DIR = "data";

    public JsonPersistenceService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initializeDataDirectory();
    }

    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR, "sheets"));
            Files.createDirectories(Paths.get(DATA_DIR, "notes"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize data directories", e);
        }
    }

    public void saveCharacterSheet(CharacterSheet sheet) throws IOException {
        File file = Paths.get(DATA_DIR, "sheets", sheet.getId() + ".json").toFile();
        objectMapper.writeValue(file, sheet);
    }

    public Optional<CharacterSheet> loadCharacterSheet(String id) {
        try {
            File file = Paths.get(DATA_DIR, "sheets", id + ".json").toFile();
            if (file.exists()) {
                return Optional.of(objectMapper.readValue(file, CharacterSheet.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveNote(CampaignNote note) throws IOException {
        File file = Paths.get(DATA_DIR, "notes", note.getId() + ".json").toFile();
        objectMapper.writeValue(file, note);
    }

    public Optional<CampaignNote> loadNote(String id) {
        try {
            File file = Paths.get(DATA_DIR, "notes", id + ".json").toFile();
            if (file.exists()) {
                return Optional.of(objectMapper.readValue(file, CampaignNote.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
