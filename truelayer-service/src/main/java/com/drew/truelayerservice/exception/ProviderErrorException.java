package com.drew.truelayerservice.exception;

public class ProviderErrorException extends TrueLayerException {
    public ProviderErrorException(String detail) {
        super(detail, 503, "provider_error");
    }
}
