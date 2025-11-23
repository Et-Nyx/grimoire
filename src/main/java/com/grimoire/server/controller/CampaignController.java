package com.grimoire.server.controller;

import com.grimoire.common.model.CampaignNote;
import com.grimoire.server.service.JsonPersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    private final JsonPersistenceService persistenceService;

    public CampaignController(JsonPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @PostMapping("/notes")
    public ResponseEntity<CampaignNote> addNote(@RequestBody CampaignNote note) {
        if (note.getId() == null) {
            note.setId(UUID.randomUUID().toString());
        }
        if (note.getTimestamp() == null) {
            note.setTimestamp(LocalDateTime.now());
        }
        
        try {
            persistenceService.saveNote(note);
            return ResponseEntity.ok(note);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<CampaignNote> getNote(@PathVariable String id) {
        return persistenceService.loadNote(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
