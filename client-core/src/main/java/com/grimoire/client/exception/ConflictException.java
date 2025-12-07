package com.grimoire.client.exception;

public class ConflictException extends GrimoireApiException {
    
    public ConflictException() {
        super("Recurso já existe", 409);
    }
    
    public ConflictException(String message) {
        super(message, 409);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, 409, cause);
    }
}