package com.drew.truelayerservice.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;
    private String expires_in;
    private String token_type;
    private String refresh_token;
    private String scope;
}
