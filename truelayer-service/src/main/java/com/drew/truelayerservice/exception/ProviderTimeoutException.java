package com.drew.truelayerservice.exception;

public class ProviderTimeoutException extends TrueLayerException {
    public ProviderTimeoutException(String detail) {
        super(detail, 504, "provider_timeout");
    }
}
