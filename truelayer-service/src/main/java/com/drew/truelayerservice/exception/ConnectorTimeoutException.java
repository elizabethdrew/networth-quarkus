package com.drew.truelayerservice.exception;

public class ConnectorTimeoutException extends TrueLayerException {
    public ConnectorTimeoutException(String detail) {
        super(detail, 504, "connector_timeout");
    }
}
