package com.drew.accountservice.kafka;

import com.drew.accountservice.service.AccountService;
import com.drew.accountservice.service.BalanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.drew.commonlibrary.dto.UpdateAccountBalanceDto;
import com.drew.commonlibrary.dto.UpdateAccountDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class KafkaConsumer {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final ObjectMapper objectMapper;

    @Inject
    public KafkaConsumer(AccountService accountService, BalanceService balanceService, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.balanceService = balanceService;
        this.objectMapper = objectMapper;
    }

    @Incoming("truelayer-account-update-topic")
    public void updateAccountFromTruelayer(String payload) throws JsonProcessingException {
        UpdateAccountDto updateAccountDto = objectMapper.readValue(payload, UpdateAccountDto.class);
        log.info("Received update for account: {}", payload);
        accountService.updateAccountFromTruelayer(updateAccountDto);
    }

    @Incoming("truelayer-account-balance-update-topic")
    public void updateAccountBalanceFromTruelayer(String payload) throws JsonProcessingException {
        UpdateAccountBalanceDto updateAccountBalanceDto = objectMapper.readValue(payload, UpdateAccountBalanceDto.class);
        log.info("Received update for account: {}", payload);
        balanceService.updateAccountBalanceFromTruelayer(updateAccountBalanceDto);
    }
}
