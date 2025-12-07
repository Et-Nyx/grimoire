package com.grimoire.client.exception;

public class ValidationException extends GrimoireApiException {
    
    public ValidationException() {
        super("Dados inválidos fornecidos", 400);
    }
    
    public ValidationException(String message) {
        super(message, 400);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, 400, cause);
    }
}