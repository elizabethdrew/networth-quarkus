package com.drew.truelayerservice.exception;

public class ScaExceededException extends TrueLayerException {
    public ScaExceededException(String detail) {
        super(detail, 403, "sca_exceeded");
    }
}
