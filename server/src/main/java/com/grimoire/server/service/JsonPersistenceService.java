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
            Files.createDirectories(Paths.get(DATA_DIR, "users"));
            Files.createDirectories(Paths.get(DATA_DIR, "campaigns"));
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

    public void deleteNote(String id) throws IOException {
        Files.deleteIfExists(Paths.get(DATA_DIR, "notes", id + ".json"));
    }

    public java.util.List<CharacterSheet> loadAllSheets() {
        java.util.List<CharacterSheet> sheets = new java.util.ArrayList<>();
        try {
            File dir = Paths.get(DATA_DIR, "sheets").toFile();
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    sheets.add(objectMapper.readValue(file, CharacterSheet.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sheets;
    }

    public java.util.List<CampaignNote> loadAllNotes() {
        java.util.List<CampaignNote> notes = new java.util.ArrayList<>();
        try {
            File dir = Paths.get(DATA_DIR, "notes").toFile();
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    notes.add(objectMapper.readValue(file, CampaignNote.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    // User Persistence
    public void saveUser(com.grimoire.common.model.User user) throws IOException {
        File file = Paths.get(DATA_DIR, "users", user.getId() + ".json").toFile();
        objectMapper.writeValue(file, user);
    }

    public Optional<com.grimoire.common.model.User> loadUser(String id) {
        try {
            File file = Paths.get(DATA_DIR, "users", id + ".json").toFile();
            if (file.exists()) {
                return Optional.of(objectMapper.readValue(file, com.grimoire.common.model.User.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<com.grimoire.common.model.User> findUserByUsername(String username) {
        try {
            File userDir = Paths.get(DATA_DIR, "users").toFile();
            File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    com.grimoire.common.model.User user = objectMapper.readValue(file, com.grimoire.common.model.User.class);
                    if (user.getUsername().equals(username)) {
                        return Optional.of(user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Campaign Persistence
    public void saveCampaign(com.grimoire.common.model.Campaign campaign) throws IOException {
        File file = Paths.get(DATA_DIR, "campaigns", campaign.getId() + ".json").toFile();
        objectMapper.writeValue(file, campaign);
    }

    public Optional<com.grimoire.common.model.Campaign> loadCampaign(String id) {
        try {
            File file = Paths.get(DATA_DIR, "campaigns", id + ".json").toFile();
            if (file.exists()) {
                return Optional.of(objectMapper.readValue(file, com.grimoire.common.model.Campaign.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public java.util.List<com.grimoire.common.model.Campaign> loadAllCampaigns() {
        java.util.List<com.grimoire.common.model.Campaign> campaigns = new java.util.ArrayList<>();
        try {
            File campaignDir = Paths.get(DATA_DIR, "campaigns").toFile();
            File[] files = campaignDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    campaigns.add(objectMapper.readValue(file, com.grimoire.common.model.Campaign.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return campaigns;
    }
}
