package com.miniproject.rookiejangter.exception;

public class LogoutException extends RuntimeException {
    public LogoutException(String message) {
        super(message);
    }
    
    public LogoutException(String message, Throwable cause) {
        super(message, cause);
    }
}