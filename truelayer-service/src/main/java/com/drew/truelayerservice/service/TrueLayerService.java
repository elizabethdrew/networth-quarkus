package com.drew.truelayerservice.service;

import com.drew.commonlibrary.dto.RequestAccountBalanceDto;
import com.drew.truelayerservice.dto.AccountDto;
import com.drew.truelayerservice.dto.CardDto;
import com.drew.truelayerservice.dto.TokenResponse;

import java.util.List;

public interface TrueLayerService {
    String authenticateNewBank(String keycloakUserId);
    TokenResponse exchangeCodeForToken(String code, String state);
    List<AccountDto> fetchAccounts(String keycloakUserId);
    List<CardDto> fetchCards(String keycloakUserId);
    void updateAccounts(String keycloakUserId);
    void updateAccountBalanceFromTruelayer(RequestAccountBalanceDto requestAccountBalanceDto);
}
