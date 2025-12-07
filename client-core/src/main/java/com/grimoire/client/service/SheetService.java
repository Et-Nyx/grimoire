package com.grimoire.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.http.GrimoireHttpClient;
import com.grimoire.common.model.CharacterSheet;

import java.util.List;
import java.util.UUID;

public class SheetService {
    
    private final GrimoireHttpClient httpClient;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    
    public SheetService(GrimoireHttpClient httpClient, AuthService authService) {
        this.httpClient = httpClient;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public CharacterSheet getSheet(String sheetId) throws GrimoireApiException {
        return httpClient.get("/sheet/" + sheetId, CharacterSheet.class);
    }
    
    public List<CharacterSheet> getSheetsByCampaign(UUID campaignId) throws GrimoireApiException {
        String response = httpClient.get("/sheet?campaignId=" + campaignId, String.class);
        
        try {
            return objectMapper.readValue(response, new TypeReference<List<CharacterSheet>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de fichas", 0, e);
        }
    }
    
    public List<CharacterSheet> getSheetsByPlayer(UUID playerId) throws GrimoireApiException {
        String response = httpClient.get("/sheet?playerId=" + playerId, String.class);
        try {
            return objectMapper.readValue(response, new TypeReference<List<CharacterSheet>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de fichas", 0, e);
        }
    }
    
    public List<CharacterSheet> getAllSheets() throws GrimoireApiException {
        String response = httpClient.get("/sheet", String.class);
        
        try {
            return objectMapper.readValue(response, new TypeReference<List<CharacterSheet>>() {});
        } catch (Exception e) {
            throw new GrimoireApiException("Erro ao processar lista de fichas", 0, e);
        }
    }
    
    public CharacterSheet createSheet(CharacterSheet sheet) throws GrimoireApiException {
        ensureLoggedIn();
        
        // Define o jogador como o usuário atual
        if (sheet.getPlayerId() == null) {
            sheet.setPlayerId(authService.getCurrentUser().getId());
        }
        
        return httpClient.post("/sheet", sheet, CharacterSheet.class);
    }
    
    public CharacterSheet updateSheet(CharacterSheet sheet) throws GrimoireApiException {
        ensureLoggedIn();
        
        return httpClient.put("/sheet/" + sheet.getId(), sheet, CharacterSheet.class);
    }
    
    public void deleteSheet(String sheetId) throws GrimoireApiException {
        ensureLoggedIn();
        
        httpClient.delete("/sheet/" + sheetId);
    }
    
    public CharacterSheet createEmptySheet(UUID campaignId, String characterName) throws GrimoireApiException {
        ensureLoggedIn();
        
        CharacterSheet sheet = CharacterSheet.builder()
                .campaignId(campaignId)
                .playerId(authService.getCurrentUser().getId())
                .name(characterName)
                .playerName(authService.getCurrentUser().getUsername())
                .system("Tormenta20")
                .level(1)
                .build();
        
        return createSheet(sheet);
    }
    
    private void ensureLoggedIn() throws GrimoireApiException {
        if (!authService.isLoggedIn()) {
            throw new GrimoireApiException("Usuário não está logado", 401);
        }
    }
}