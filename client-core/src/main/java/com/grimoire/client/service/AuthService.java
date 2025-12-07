package com.grimoire.client.service;

import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.http.GrimoireHttpClient;
import com.grimoire.common.model.User;

import java.util.UUID;

public class AuthService {
    
    private final GrimoireHttpClient httpClient;
    private User currentUser;
    private String currentToken;
    
    public AuthService(GrimoireHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public User login(String username, String password) throws GrimoireApiException {
        User loginRequest = User.builder()
                .username(username)
                .password(password)
                .build();
        
        User user = httpClient.post("/auth/login", loginRequest, User.class);
        
        if (user != null) {
            this.currentUser = user;
            // TODO: Se o servidor retornar um token JWT, armazená-lo aqui
            // this.currentToken = user.getToken();
            // httpClient.setAuthToken(currentToken);
        }
        
        return user;
    }
    
    public User register(String username, String password) throws GrimoireApiException {
        User registerRequest = User.builder()
                .username(username)
                .password(password)
                .build();
        
        return httpClient.post("/auth/register", registerRequest, User.class);
    }
    
    public User getUser(UUID userId) throws GrimoireApiException {
        return httpClient.get("/auth/user/" + userId, User.class);
    }
    
    public User updateUser(User user) throws GrimoireApiException {
        return httpClient.put("/auth/user/" + user.getId(), user, User.class);
    }
    
    public void deleteUser(UUID userId) throws GrimoireApiException {
        httpClient.delete("/auth/user/" + userId);
    }
    
    public void logout() {
        this.currentUser = null;
        this.currentToken = null;
        httpClient.clearAuthToken();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getCurrentToken() {
        return currentToken;
    }
}