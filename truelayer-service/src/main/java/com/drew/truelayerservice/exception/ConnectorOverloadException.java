package com.drew.truelayerservice.exception;

public class ConnectorOverloadException extends TrueLayerException {
    public ConnectorOverloadException(String detail) {
        super(detail, 503, "connector_overload");
    }
}
