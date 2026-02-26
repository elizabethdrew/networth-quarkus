package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountBalanceDto {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("available")
    private Long available;

    @JsonProperty("current")
    private Long current;

    @JsonProperty("overdraft")
    private Long overdraft;

    @JsonProperty("update_timestamp")
    private String updateTimestamp;

}
