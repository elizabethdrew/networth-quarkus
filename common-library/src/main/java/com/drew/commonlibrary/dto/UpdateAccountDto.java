package com.drew.commonlibrary.dto;

public record UpdateAccountDto (
        String keycloakUserId,
    String accountNumber,
    String accountId,
    String accountType,
    String displayName,
    String currency,
    String provider,
    String updateTimestamp
){

}