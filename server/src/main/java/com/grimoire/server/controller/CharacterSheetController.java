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
    
    // TODO: Implement specific resource patching if needed, or rely on full update for now.
}
