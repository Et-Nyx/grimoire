package com.grimoire.server.controller;

import com.grimoire.common.model.Campaign;
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
        com.grimoire.server.util.ServerLogger.logInfo("Received join request for campaign: " + id + ", user: " + userId);
        try {
            UUID userUuid = UUID.fromString(userId);
            return persistenceService.loadCampaign(id)
                    .map(campaign -> {
                        com.grimoire.server.util.ServerLogger.logInfo("Campaign found: " + campaign.getName());
                        campaign.addPlayer(userUuid);
                        try {
                            persistenceService.saveCampaign(campaign);
                            com.grimoire.server.util.ServerLogger.logInfo("Campaign saved with new player.");
                            return ResponseEntity.ok(campaign);
                        } catch (IOException e) {
                            com.grimoire.server.util.ServerLogger.logError("Error saving campaign", e);
                            return ResponseEntity.internalServerError().<Campaign>build();
                        }
                    })
                    .orElseGet(() -> {
                        com.grimoire.server.util.ServerLogger.logInfo("Campaign not found: " + id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (IllegalArgumentException e) {
            com.grimoire.server.util.ServerLogger.logError("Invalid UUID format", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable String id, @RequestParam String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            return persistenceService.loadCampaign(id)
                    .map(campaign -> {
                        if (!campaign.getOwnerId().equals(userUuid)) {
                            return ResponseEntity.status(403).<Void>build();
                        }
                        try {
                            persistenceService.deleteCampaign(id);
                            return ResponseEntity.ok().<Void>build();
                        } catch (IOException e) {
                            return ResponseEntity.internalServerError().<Void>build();
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
