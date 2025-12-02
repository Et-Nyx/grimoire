package com.grimoire.server.controller;

import com.grimoire.common.model.CharacterSheet;
import com.grimoire.server.service.JsonPersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/sheet")
public class CharacterSheetController {

    private final JsonPersistenceService persistenceService;

    public CharacterSheetController(JsonPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterSheet> getSheet(@PathVariable String id) {
        Optional<CharacterSheet> sheet = persistenceService.loadCharacterSheet(id);
        return sheet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CharacterSheet> saveSheet(@RequestBody CharacterSheet sheet) {
        try {
            persistenceService.saveCharacterSheet(sheet);
            return ResponseEntity.ok(sheet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<java.util.List<CharacterSheet>> listSheets(@RequestParam(required = false) String campaignId) {
        java.util.List<CharacterSheet> allSheets = persistenceService.loadAllSheets();
        
        if (campaignId != null) {
            try {
                java.util.UUID cId = java.util.UUID.fromString(campaignId);
                java.util.List<CharacterSheet> filtered = allSheets.stream()
                        .filter(s -> cId.equals(s.getCampaignId()))
                        .collect(java.util.stream.Collectors.toList());
                return ResponseEntity.ok(filtered);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        return ResponseEntity.ok(allSheets);
    }
}
