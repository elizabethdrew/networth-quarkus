package com.drew.truelayerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardResponseDto {
    private List<CardDto> results;
    private String status;
}
