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

    public List<CharacterSheet> listSheets(UUID campaignId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/sheet?campaignId=" + campaignId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<CharacterSheet>>() {});
        }
        return List.of();
    }

    // Auth
    public User login(String username, String password) throws Exception {
        User loginRequest = User.builder()
                .username(username)
                .password(password)
                .build();
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
        User registerRequest = User.builder()
                .username(username)
                .password(password)
                .build();
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

    public User getUser(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/user/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), User.class);
        }
        return null;
    }

    public void logout() {
        this.currentUser = null;
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

        Campaign campaign = Campaign.builder()
                .name(name)
                .description(description)
                .ownerId(currentUser.getId())
                .build();
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

    public com.grimoire.common.model.SessionNote saveNote(String sessionId, com.grimoire.common.model.SessionNote note) throws Exception {
        String json = objectMapper.writeValueAsString(note);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session/" + sessionId + "/notes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), com.grimoire.common.model.SessionNote.class);
        }
        return null;
    }

    public void deleteNote(String noteId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session/notes/" + noteId))
                .DELETE()
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    public List<com.grimoire.common.model.SessionNote> listNotes(String sessionId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session/" + sessionId + "/notes"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<com.grimoire.common.model.SessionNote>>() {});
        }
        return List.of();
    }
}
