package com.drew.accountservice.service.impl;

import com.drew.accountservice.dto.AccountInputDto;
import com.drew.accountservice.dto.AccountOutputDto;
import com.drew.accountservice.dto.AccountUpdateDto;
import com.drew.accountservice.entity.Account;
import com.drew.accountservice.entity.Balance;
import com.drew.accountservice.kafka.KafkaService;
import com.drew.accountservice.mapper.AccountMapper;
import com.drew.accountservice.repository.AccountRepository;
import com.drew.accountservice.repository.BalanceRepository;
import com.drew.accountservice.service.AccountService;
import com.drew.commonlibrary.dto.RequestAccountBalanceDto;
import com.drew.commonlibrary.dto.UpdateAccountDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final BalanceRepository balanceRepository;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final KafkaService kafkaService;


    @Inject
    public AccountServiceImpl(AccountRepository accountRepository, BalanceRepository balanceRepository, KafkaService kafkaService) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.kafkaService = kafkaService;
    }

    @Override
    public AccountOutputDto createAccount(String keycloakUserId, AccountInputDto accountInputDto) {

        log.info("Keycloak User ID: " + keycloakUserId);

        Account newAccount = accountMapper.toEntity(keycloakUserId, accountInputDto);

        newAccount.setDateCreated(LocalDateTime.now());
        newAccount.setDateUpdated(LocalDateTime.now());

        Account savedAccount = accountRepository.save(newAccount);

        // If ISA account, tell ISA Service
        if (savedAccount.getType().toString().contains("ISA")) {
            log.info("New account is an ISA");
//            kafkaService.newAccountKafka("sendNewIsaAccount-out-0", savedAccount);
        } else {
            log.info("New account is not an ISA");
        }

        return accountMapper.toOutputDto(savedAccount);
    }

    @Override
    public List<AccountOutputDto> getUserAccounts(String keycloakUserId) {
        List<Account> accounts = accountRepository.findAllByKeycloakUserId(keycloakUserId);
        return accounts.stream().map(account -> {
            AccountOutputDto dto = accountMapper.toOutputDto(account);
            List<Balance> latestBalances = balanceRepository.findLatestBalanceByAccountId(account.getAccountId());
            if (!latestBalances.isEmpty()) {
                Balance latestBalance = latestBalances.get(0);
                dto.setCurrentBalance(latestBalance.getBalance());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<AccountOutputDto> getAccountByIdAndKeycloakId(Long accountId, String keycloakUserId) {
        return accountRepository.findByAccountIdAndKeycloakUserId(accountId, keycloakUserId)
                .map(account -> {
                    AccountOutputDto dto = accountMapper.toOutputDto(account);
                    List<Balance> latestBalances = balanceRepository.findLatestBalanceByAccountId(account.getAccountId());
                    latestBalances.stream().findFirst().ifPresent(latestBalance ->
                            dto.setCurrentBalance(latestBalance.getBalance())
                    );
                    return dto;
                });
    }

    @Override
    public Optional<AccountOutputDto> updateAccountByIdAndKeycloakId(Long accountId, String keycloakUserId, AccountUpdateDto accountUpdateDto) {
        return accountRepository.findByAccountIdAndKeycloakUserId(accountId, keycloakUserId)
                .map(account -> {
                    accountMapper.updateAccount(accountUpdateDto, account);
                    account.setDateUpdated(LocalDateTime.now());
                    Account updatedAccount = accountRepository.save(account);

                    return accountMapper.toOutputDto(updatedAccount);
                });
    }


    @Override
    public boolean softDeleteAccount(Long accountId, String keycloakUserId) {
        return accountRepository.findByAccountIdAndKeycloakUserId(accountId, keycloakUserId)
                .map(account -> {
                    account.setStatus(Account.AccountStatus.ARCHIVED);
                    account.setDateUpdated(LocalDateTime.now());
                    accountRepository.save(account);
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional
    public void updateAccountFromTruelayer(UpdateAccountDto updateAccountDto) {
        log.info("Starting update account from Truelayer");
        Optional<Account> existingAccountOpt = accountRepository.findByTruelayerAccountIdAndKeycloakUserId(updateAccountDto.accountId(), updateAccountDto.keycloakUserId());
        log.info("Existing account optional: " + existingAccountOpt);
        Account account = existingAccountOpt
                .map(existingAccount -> {
                    log.info("Existing account being updated");
                    updateExistingAccount(existingAccount, updateAccountDto);
                    return existingAccount;
                })
                .orElseGet(() -> {
                    log.info("New account being added");
                    Account newAccount = new Account();
                    updateExistingAccount(newAccount, updateAccountDto);
                    newAccount.setKeycloakUserId(updateAccountDto.keycloakUserId());
                    newAccount.setDateCreated(LocalDateTime.now());
                    return newAccount;
                });

        log.info("Account: " + account);
        account.setDateUpdated(LocalDateTime.now());
        accountRepository.save(account);

        RequestAccountBalanceDto requestAccountBalanceDto = new RequestAccountBalanceDto(
                account.getTruelayerAccountId(),
                account.getTruelayerAccountType(),
                account.getKeycloakUserId()
        );
        kafkaService.requestAccountBalanceKafka(requestAccountBalanceDto);
    }

    private void updateExistingAccount(Account account, UpdateAccountDto updateAccountDto) {
        account.setTruelayerAccountId(updateAccountDto.accountId());
        account.setTruelayerAccountNumber(updateAccountDto.accountNumber());
        account.setTruelayerAccountType(updateAccountDto.accountType());
        account.setAccountNickname(updateAccountDto.displayName());
        account.setCurrency(updateAccountDto.currency());
        account.setProvider(updateAccountDto.provider());
        //account.setTruelayerDateUpdated(LocalDateTime.parse(updateAccountDto.updateTimestamp()));
    }

}
