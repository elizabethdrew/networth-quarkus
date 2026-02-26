package com.drew.accountservice.service;

import com.drew.accountservice.dto.BalanceDto;
import com.drew.accountservice.dto.BalanceHistoryDto;
import com.drew.commonlibrary.dto.UpdateAccountBalanceDto;

public interface BalanceService {
    BalanceHistoryDto getBalanceHistory(String keycloakUserId, Long accountId);
    BalanceDto getBalanceById(String keycloakUserId, Long accountId, Long balanceId);
    void updateAccountBalanceFromTruelayer(UpdateAccountBalanceDto updateAccountBalanceDto);
}



