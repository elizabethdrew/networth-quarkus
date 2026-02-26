package com.drew.truelayerservice.exception;

public class EndpointNotSupportedException extends TrueLayerException {
    public EndpointNotSupportedException(String detail) {
        super(detail, 501, "endpoint_not_supported");
    }
}
