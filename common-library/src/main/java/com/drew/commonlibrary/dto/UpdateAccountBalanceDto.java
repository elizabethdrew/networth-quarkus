package com.drew.commonlibrary.dto;

public record UpdateAccountBalanceDto(
    String accountId,
    String keycloakUserId,
    String currency,
    Long current,
    String updateTimestamp
){

}