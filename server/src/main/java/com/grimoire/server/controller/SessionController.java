package com.grimoire.server.controller;

import com.grimoire.common.model.Session;
import com.grimoire.common.model.SessionNote;
import com.grimoire.server.service.JsonPersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final JsonPersistenceService persistenceService;

    public SessionController(JsonPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @GetMapping
    public ResponseEntity<List<Session>> listSessions(@RequestParam(required = false) String campaignId, @RequestParam(required = false) String userId) {
        try {
            if (campaignId != null) {
                UUID campaignUuid = UUID.fromString(campaignId);
                List<Session> sessions = persistenceService.loadSessionsByCampaign(campaignUuid);
                return ResponseEntity.ok(sessions);
            } else if (userId != null) {
                UUID userUuid = UUID.fromString(userId);
                List<com.grimoire.common.model.Campaign> allCampaigns = persistenceService.loadAllCampaigns();
                List<UUID> userCampaignIds = allCampaigns.stream()
                        .filter(c -> c.getPlayerIds().contains(userUuid))
                        .map(com.grimoire.common.model.Campaign::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                List<Session> allSessions = persistenceService.loadAllSessions();
                List<Session> userSessions = allSessions.stream()
                        .filter(s -> userCampaignIds.contains(s.getCampaignId()))
                        .collect(java.util.stream.Collectors.toList());
                        
                return ResponseEntity.ok(userSessions);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        if (session.getId() == null || session.getId().isEmpty()) {
            session.setId(UUID.randomUUID().toString());
        }
        if (session.getDate() == null) {
            session.setDate(LocalDateTime.now());
        }
        
        try {
            persistenceService.saveSession(session);
            return ResponseEntity.ok(session);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable String id) {
        return persistenceService.loadSession(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable String id) {
        try {
            persistenceService.deleteSession(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{sessionId}/notes")
    public ResponseEntity<SessionNote> addNote(@PathVariable String sessionId, @RequestBody SessionNote note) {
        if (note.getId() == null || note.getId().isEmpty()) {
            note.setId(UUID.randomUUID().toString());
        }
        note.setSessionId(sessionId);
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

    @GetMapping("/{sessionId}/notes")
    public ResponseEntity<List<SessionNote>> listNotes(@PathVariable String sessionId) {
        List<SessionNote> notes = persistenceService.loadNotesBySession(sessionId);
        return ResponseEntity.ok(notes);
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable String noteId) {
        try {
            persistenceService.deleteNote(noteId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
