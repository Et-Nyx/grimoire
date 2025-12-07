package com.grimoire.client.exception;

public class GrimoireApiException extends Exception {
    private final int statusCode;
    private final String errorMessage;

    public GrimoireApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorMessage = message;
    }

    public GrimoireApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorMessage = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}