package com.drew.truelayerservice.exception;

public class UnauthorizedException extends TrueLayerException {
    public UnauthorizedException(String detail) {
        super("The credentials or token are no longer valid: " + detail, 401, "unauthorized");
    }
}
