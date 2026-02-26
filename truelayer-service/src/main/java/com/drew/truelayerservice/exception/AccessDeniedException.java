package com.drew.truelayerservice.exception;

public class AccessDeniedException extends TrueLayerException {
    public AccessDeniedException(String detail) {
        super(detail, 403, "access_denied");
    }
}
