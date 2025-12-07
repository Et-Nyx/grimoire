package com.grimoire.client.exception;

public class UnauthorizedException extends GrimoireApiException {
    
    public UnauthorizedException() {
        super("Credenciais inválidas", 401);
    }
    
    public UnauthorizedException(String message) {
        super(message, 401);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}