package com.drew.truelayerservice.exception;

public class InvalidTokenException extends TrueLayerException {
    public InvalidTokenException(String detail) {
        super("The token is no longer valid: " + detail, 401, "invalid_token");
    }
}
