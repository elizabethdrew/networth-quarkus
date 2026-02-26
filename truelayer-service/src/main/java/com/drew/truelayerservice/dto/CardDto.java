package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardDto {
    @JsonProperty("update_timestamp")
    private String updateTimestamp;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("partial_card_number")
    private String partialCardNumber;

    @JsonProperty("name_on_card")
    private String nameOnCard;

    @JsonProperty("provider")
    private ProviderDto provider;
}
