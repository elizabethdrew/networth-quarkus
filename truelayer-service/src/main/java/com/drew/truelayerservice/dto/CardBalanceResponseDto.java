package com.drew.truelayerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardBalanceResponseDto {
    private List<CardBalanceDto> results;
    private String status;
}
