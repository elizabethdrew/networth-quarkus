package com.drew.truelayerservice.exception;

public abstract class TrueLayerException extends RuntimeException {
    private final int status;
    private final String errorCode;

    protected TrueLayerException(String message, int status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
