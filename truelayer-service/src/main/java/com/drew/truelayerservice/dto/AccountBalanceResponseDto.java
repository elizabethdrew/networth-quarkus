package com.drew.truelayerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountBalanceResponseDto {
    private List<AccountBalanceDto> results;
    private String status;
}
