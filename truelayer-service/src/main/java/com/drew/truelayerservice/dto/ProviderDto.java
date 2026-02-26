package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProviderDto {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("logo_uri")
    private String logoUri;

}
