package com.networth.userservice.exception;

public class AuthenticationServiceException extends RuntimeException {
    public AuthenticationServiceException(String message, Exception e) {
        super(message, e);
    }
}
