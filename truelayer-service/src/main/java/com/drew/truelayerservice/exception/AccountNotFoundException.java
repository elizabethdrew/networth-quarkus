package com.drew.truelayerservice.exception;

public class AccountNotFoundException extends TrueLayerException {
    public AccountNotFoundException(String detail) {
        super("The requested account cannot be found: " + detail, 404, "account_not_found");
    }
}
