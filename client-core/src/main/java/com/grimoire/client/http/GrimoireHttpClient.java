package com.grimoire.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grimoire.client.exception.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class GrimoireHttpClient {
    
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;
    
    public GrimoireHttpClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    public void clearAuthToken() {
        this.authToken = null;
    }
    
    public <T> T get(String endpoint, Class<T> responseType) throws GrimoireApiException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET();
            
            addAuthHeader(builder);
            HttpRequest request = builder.build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);
            
        } catch (IOException | InterruptedException e) {
            throw new GrimoireApiException("Erro de conexão: " + e.getMessage(), 0, e);
        }
    }
    
    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws GrimoireApiException {
        return sendWithBody(endpoint, "POST", requestBody, responseType);
    }
    
    public <T> T put(String endpoint, Object requestBody, Class<T> responseType) throws GrimoireApiException {
        return sendWithBody(endpoint, "PUT", requestBody, responseType);
    }
    
    public void delete(String endpoint) throws GrimoireApiException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .DELETE();
            
            addAuthHeader(builder);
            HttpRequest request = builder.build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleResponse(response, Void.class);
            
        } catch (IOException | InterruptedException e) {
            throw new GrimoireApiException("Erro de conexão: " + e.getMessage(), 0, e);
        }
    }
    
    private <T> T sendWithBody(String endpoint, String method, Object requestBody, Class<T> responseType) throws GrimoireApiException {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .header("Content-Type", "application/json");
            
            if ("POST".equals(method)) {
                builder.POST(HttpRequest.BodyPublishers.ofString(json));
            } else if ("PUT".equals(method)) {
                builder.PUT(HttpRequest.BodyPublishers.ofString(json));
            }
            
            addAuthHeader(builder);
            HttpRequest request = builder.build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);
            
        } catch (IOException | InterruptedException e) {
            throw new GrimoireApiException("Erro de conexão: " + e.getMessage(), 0, e);
        }
    }
    
    private void addAuthHeader(HttpRequest.Builder builder) {
        if (authToken != null && !authToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken);
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) throws GrimoireApiException {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        
        if (statusCode >= 200 && statusCode < 300) {
            if (responseType == Void.class || responseBody.isEmpty()) {
                return null;
            }
            
            if (responseType == String.class) {
                return (T) responseBody;
            }
            
            try {
                return objectMapper.readValue(responseBody, responseType);
            } catch (IOException e) {
                throw new GrimoireApiException("Erro ao processar resposta do servidor", statusCode, e);
            }
        }
        
        // Tratamento de erros baseado no status code
        String errorMessage = extractErrorMessage(responseBody);
        
        switch (statusCode) {
            case 400:
                if ("Erro desconhecido".equals(errorMessage)) {
                    errorMessage = "Requisição inválida. Verifique os dados informados.";
                }
                throw new ValidationException(errorMessage);
            case 401:
                if ("Erro desconhecido".equals(errorMessage)) {
                    errorMessage = "Credenciais inválidas ou não autorizado.";
                }
                throw new UnauthorizedException(errorMessage);
            case 404:
                if ("Erro desconhecido".equals(errorMessage)) {
                    errorMessage = "Objeto não encontrado no servidor.";
                }
                throw new NotFoundException(errorMessage);
            case 409:
                if ("Erro desconhecido".equals(errorMessage)) {
                    errorMessage = "Conflito: o recurso já existe ou está em uso.";
                }
                throw new ConflictException(errorMessage);
            case 500:
                throw new GrimoireApiException("Erro interno do servidor", statusCode);
            default:
                throw new GrimoireApiException("Erro HTTP " + statusCode + ": " + errorMessage, statusCode);
        }
    }
    
    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "Erro desconhecido";
        }
        
        try {
            Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);
            Object erro = errorResponse.get("erro");
            if (erro != null) {
                return erro.toString();
            }
            
            Object message = errorResponse.get("message");
            if (message != null) {
                return message.toString();
            }
        } catch (IOException e) {
            // Se não conseguir parsear como JSON, retorna o corpo da resposta
        }
        
        return responseBody;
    }
}