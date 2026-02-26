package com.drew.accountservice.service.impl;

import com.drew.accountservice.dto.NetworthOutputDto;
import com.drew.accountservice.entity.Account;
import com.drew.accountservice.entity.Balance;
import com.drew.accountservice.repository.AccountRepository;
import com.drew.accountservice.repository.BalanceRepository;
import com.drew.accountservice.service.NetworthService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@ApplicationScoped
public class NetworthServiceImpl implements NetworthService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    public NetworthServiceImpl(AccountRepository accountRepository, BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
    }

    @Override
    public NetworthOutputDto getUserNetworth(String keycloakUserId) {
        List<Account> accounts = accountRepository.findAllByKeycloakUserId(keycloakUserId);
        BigDecimal assets = BigDecimal.ZERO;
        BigDecimal liabilities = BigDecimal.ZERO;

        for (Account account : accounts) {
            List<Balance> latestBalances = balanceRepository.findLatestBalanceByAccountId(account.getAccountId());
            if (!latestBalances.isEmpty()) {
                Balance latestBalance = latestBalances.get(0);
                BigDecimal balance = latestBalance.getBalance();

                if ("SAVINGS".equals(account.getTruelayerAccountType()) || "TRANSACTION".equals(account.getTruelayerAccountType())) {
                    assets = assets.add(balance);
                }
                else if ("CARD".equals(account.getTruelayerAccountType())) {
                    liabilities = liabilities.add(balance);
                }
            }
        }

        BigDecimal total = assets.subtract(liabilities);

        NetworthOutputDto networthOutputDto = new NetworthOutputDto();
        networthOutputDto.setAssets(assets);
        networthOutputDto.setLiabilities(liabilities);
        networthOutputDto.setTotal(total);

        return networthOutputDto;
    }
}
