package com.drew.truelayerservice.exception;

public class ProviderRequestLimitExceededException extends TrueLayerException {
    public ProviderRequestLimitExceededException(String detail) {
        super(detail, 429, "provider_request_limit_exceeded");
    }
}
