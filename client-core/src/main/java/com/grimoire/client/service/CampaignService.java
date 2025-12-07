package com.grimoire.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.http.GrimoireHttpClient;
import com.grimoire.common.model.Campaign;

import java.util.List;
import java.util.UUID;

public class CampaignService {
    
    private final GrimoireHttpClient httpClient;
    private final AuthService authService;
    private final SheetService sheetService;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    
    public CampaignService(GrimoireHttpClient httpClient, AuthService authService, SheetService sheetService, SessionService sessionService) {
        this.httpClient = httpClient;
        this.authService = authService;
        this.sheetService = sheetService;
        this.sessionService = sessionService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public List<Campaign> getMyCampaigns() throws GrimoireApiException {
        ensureLoggedIn();
        
        UUID userId = authService.getCurrentUser().getId();
        return getListFromEndpoint("/campaign?userId=" + userId);
    }
    
    public List<Campaign> getCampaignsByPlayer(UUID userId) throws GrimoireApiException {
        ensureLoggedIn();
        return getListFromEndpoint("/campaign?userId=" + userId);
    }
    
    private List<Campaign> getListFromEndpoint(String endpoint) throws GrimoireApiException {
        String response = httpClient.get(endpoint, String.class);
        
        try {
            return objectMapper.readValue(response, new TypeReference<List<Campaign>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de campanhas", 0, e);
        }
    }
    
    public Campaign createCampaign(String name, String description, String system) throws GrimoireApiException {
        ensureLoggedIn();
        
        Campaign newCampaign = Campaign.builder()
                .name(name)
                .description(description)
                .system(system)
                .ownerId(authService.getCurrentUser().getId())
                .build();
        
        Campaign createdCampaign = httpClient.post("/campaign", newCampaign, Campaign.class);
        
        // Create Session Zero
        try {
            com.grimoire.common.model.Session sessionZero = com.grimoire.common.model.Session.builder()
                .campaignId(createdCampaign.getId())
                .title("Sessão Zero")
                .summary("Criação de personagens e introdução à campanha.")
                .date(java.time.LocalDateTime.now())
                .build();
            sessionService.createSession(sessionZero);
        } catch (Exception e) {
            System.err.println("Failed to create Session Zero: " + e.getMessage());
        }
        
        return createdCampaign;
    }
    
    public Campaign updateCampaign(Campaign campaign) throws GrimoireApiException {
        ensureLoggedIn();
        return httpClient.post("/campaign", campaign, Campaign.class);
    }
    
    public Campaign joinCampaign(String campaignId) throws GrimoireApiException {
        ensureLoggedIn();
        
        UUID userId = authService.getCurrentUser().getId();
        String endpoint = "/campaign/" + campaignId + "/join?userId=" + userId;
        
        Campaign joinedCampaign = httpClient.post(endpoint, null, Campaign.class);
        
        // Create a blank character sheet for the new campaign
        try {
            com.grimoire.common.model.CharacterSheet newSheet = com.grimoire.common.model.CharacterSheet.builder()
                .id(java.util.UUID.randomUUID().toString())
                .campaignId(joinedCampaign.getId())
                .playerId(userId)
                .name("Novo Personagem")
                .system(joinedCampaign.getSystem()) // Set system from campaign
                .level(1)
                .build();
            sheetService.createSheet(newSheet);
        } catch (Exception e) {
            // Log error but don't fail the join? Or maybe warn the user.
            // For now, we proceed as the user is already in the campaign.
            com.grimoire.client.util.ClientLogger.logError("Failed to create initial character sheet", e);
            System.err.println("Failed to create initial character sheet: " + e.getMessage());
        }
        
        return joinedCampaign;
    }
    
    public Campaign getCampaign(UUID campaignId) throws GrimoireApiException {
        return httpClient.get("/campaign/" + campaignId, Campaign.class);
    }
    
    public void deleteCampaign(UUID campaignId) throws GrimoireApiException {
        ensureLoggedIn();
        UUID userId = authService.getCurrentUser().getId();
        httpClient.delete("/campaign/" + campaignId + "?userId=" + userId);
    }
    
    private void ensureLoggedIn() throws GrimoireApiException {
        if (!authService.isLoggedIn()) {
            throw new GrimoireApiException("Usuário não está logado", 401);
        }
    }
}