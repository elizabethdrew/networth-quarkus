package com.drew.truelayerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponseDto {
    private List<AccountDto> results;
    private String status;
}
