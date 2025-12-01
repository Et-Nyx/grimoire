package com.grimoire.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class ApiClient {
    
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private User currentUser;
    
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    public CharacterSheet getSheet(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/sheet/" + id))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), CharacterSheet.class);
        }
        return null;
    }
    
    public CharacterSheet saveSheet(CharacterSheet sheet) throws Exception {
        String json = objectMapper.writeValueAsString(sheet);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/sheet"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), CharacterSheet.class);
        }
        return null;
    }

    // Auth
    public User login(String username, String password) throws Exception {
        User loginRequest = new User(username, password);
        String json = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            this.currentUser = objectMapper.readValue(response.body(), User.class);
            return this.currentUser;
        }
        return null;
    }

    public User register(String username, String password) throws Exception {
        User registerRequest = new User(username, password);
        String json = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), User.class);
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Campaign
    public List<Campaign> listMyCampaigns() throws Exception {
        if (currentUser == null) throw new IllegalStateException("Not logged in");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/campaign?userId=" + currentUser.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Campaign>>() {});
        }
        return List.of();
    }

    public Campaign createCampaign(String name, String description) throws Exception {
        if (currentUser == null) throw new IllegalStateException("Not logged in");

        Campaign campaign = new Campaign(name, description, currentUser.getId());
        String json = objectMapper.writeValueAsString(campaign);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/campaign"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Campaign.class);
        }
        return null;
    }

    public Campaign joinCampaign(String campaignId) throws Exception {
        if (currentUser == null) throw new IllegalStateException("Not logged in");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/campaign/" + campaignId + "/join?userId=" + currentUser.getId()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Campaign.class);
        }
        return null;
    }
}
