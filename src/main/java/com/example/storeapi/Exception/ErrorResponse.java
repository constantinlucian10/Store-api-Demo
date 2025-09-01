package com.example.storeapi.Exception;

import java.time.Instant;

public class ErrorResponse {

    private String timestamp;
    private String error;
    private String details;

    public ErrorResponse(String error, String details) {
        this.timestamp = Instant.now().toString();
        this.error = error;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getDetails() {
        return details;
    }
}
