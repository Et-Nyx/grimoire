package com.grimoire.client.exception;

public class NotFoundException extends GrimoireApiException {
    
    public NotFoundException() {
        super("Recurso não encontrado", 404);
    }
    
    public NotFoundException(String message) {
        super(message, 404);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, 404, cause);
    }
}