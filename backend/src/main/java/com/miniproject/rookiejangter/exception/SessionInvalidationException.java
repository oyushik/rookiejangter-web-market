package com.miniproject.rookiejangter.exception;

public class SessionInvalidationException extends RuntimeException {
    public SessionInvalidationException(String message) {
        super(message);
    }
    
    public SessionInvalidationException(String message, Throwable cause) {
        super(message, cause);
    }
}