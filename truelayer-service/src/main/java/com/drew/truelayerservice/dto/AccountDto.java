package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountDto {
    @JsonProperty("update_timestamp")
    private String updateTimestamp;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("account_number")
    private AccountNumberDto accountNumber;

    @JsonProperty("provider")
    private ProviderDto provider;
}