package com.grimoire.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.http.GrimoireHttpClient;
import com.grimoire.common.model.Session;
import com.grimoire.common.model.SessionNote;

import java.util.List;
import java.util.UUID;

public class SessionService {
    
    private final GrimoireHttpClient httpClient;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    
    public SessionService(GrimoireHttpClient httpClient, AuthService authService) {
        this.httpClient = httpClient;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public List<Session> listByCampaign(String campaignId) throws GrimoireApiException {
        ensureLoggedIn();
        
        String response = httpClient.get("/session?campaignId=" + campaignId, String.class);
        
        try {
            return objectMapper.readValue(response, new TypeReference<List<Session>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de sessões", 0, e);
        }
    }
    
    public List<Session> getSessionsByPlayer(UUID playerId) throws GrimoireApiException {
        ensureLoggedIn();
        String response = httpClient.get("/session?userId=" + playerId, String.class);
        try {
            return objectMapper.readValue(response, new TypeReference<List<Session>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de sessões", 0, e);
        }
    }
    
    public Session createSession(Session session) throws GrimoireApiException {
        ensureLoggedIn();
        
        if (session.getCampaignId() == null) {
            throw new GrimoireApiException("Campaign ID é obrigatório", 400);
        }
        
        return httpClient.post("/session", session, Session.class);
    }
    
    public Session getSession(String sessionId) throws GrimoireApiException {
        ensureLoggedIn();
        return httpClient.get("/session/" + sessionId, Session.class);
    }
    
    public void deleteSession(String sessionId) throws GrimoireApiException {
        ensureLoggedIn();
        httpClient.delete("/session/" + sessionId);
    }
    
    public List<SessionNote> getSessionNotes(String sessionId) throws GrimoireApiException {
        ensureLoggedIn();
        
        String response = httpClient.get("/session/" + sessionId + "/notes", String.class);
        
        try {
            return objectMapper.readValue(response, new TypeReference<List<SessionNote>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar notas da sessão", 0, e);
        }
    }
    
    public SessionNote createNote(String sessionId, String author, String content, boolean isPublic) throws GrimoireApiException {
        ensureLoggedIn();
        
        SessionNote note = SessionNote.builder()
                .sessionId(sessionId)
                .author(author)
                .content(content)
                .isPublic(isPublic)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return httpClient.post("/session/" + sessionId + "/notes", note, SessionNote.class);
    }
    
    public void deleteNote(String noteId) throws GrimoireApiException {
        ensureLoggedIn();
        httpClient.delete("/session/notes/" + noteId);
    }
    
    private void ensureLoggedIn() throws GrimoireApiException {
        if (!authService.isLoggedIn()) {
            throw new GrimoireApiException("Usuário não está logado", 401);
        }
    }
}
