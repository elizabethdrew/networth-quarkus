package com.drew.truelayerservice.exception;

public class ProviderRateLimitExceededException extends TrueLayerException {
    public ProviderRateLimitExceededException(String detail) {
        super(detail, 429, "provider_too_many_requests");
    }
}
