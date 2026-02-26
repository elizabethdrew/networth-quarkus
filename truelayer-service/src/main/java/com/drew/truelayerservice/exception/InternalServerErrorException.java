package com.drew.truelayerservice.exception;

public class InternalServerErrorException extends TrueLayerException {
    public InternalServerErrorException(String detail) {
        super("Internal server error: " + detail, 500, "internal_server_error");
    }
}
