package com.grimoire.server.controller;

import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.CampaignNote;
import com.grimoire.server.service.JsonPersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        if (campaign.getId() == null) {
            campaign.setId(UUID.randomUUID());
        }
        // Ensure the owner is also a player
        if (campaign.getOwnerId() != null) {
            campaign.addPlayer(campaign.getOwnerId());
        }
        
        try {
            persistenceService.saveCampaign(campaign);
            return ResponseEntity.ok(campaign);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> listCampaigns(@RequestParam String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            List<Campaign> allCampaigns = persistenceService.loadAllCampaigns();
            List<Campaign> userCampaigns = allCampaigns.stream()
                    .filter(c -> c.getPlayerIds().contains(userUuid))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userCampaigns);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Campaign> joinCampaign(@PathVariable String id, @RequestParam String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            return persistenceService.loadCampaign(id)
                    .map(campaign -> {
                        campaign.addPlayer(userUuid);
                        try {
                            persistenceService.saveCampaign(campaign);
                            return ResponseEntity.ok(campaign);
                        } catch (IOException e) {
                            return ResponseEntity.internalServerError().<Campaign>build();
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
