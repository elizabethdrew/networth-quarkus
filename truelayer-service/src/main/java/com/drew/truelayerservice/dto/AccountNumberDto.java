package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountNumberDto {
    @JsonProperty("iban")
    private String iban;

    @JsonProperty("swift_bic")
    private String swiftBic;

    @JsonProperty("number")
    private String number;

    @JsonProperty("sort_code")
    private String sortCode;
}
