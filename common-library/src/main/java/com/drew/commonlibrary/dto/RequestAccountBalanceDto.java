package com.drew.commonlibrary.dto;

public record RequestAccountBalanceDto(
    String accountId,
    String accountType,
    String keycloakUserId
){

}