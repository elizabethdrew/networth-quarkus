package com.drew.accountservice.dto;

import com.drew.accountservice.entity.Account;
import com.drew.commonlibrary.types.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountOutputDto {
    private Long accountId;
    private String accountNickname;
    private String truelayerAccountNumber;
    private String truelayerAccountType;
    private String provider;
    private AccountType type;
    private String currency;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private Long percentageOwnership;
    private Boolean fixedTerm;
    private LocalDateTime fixedTermEndDate;
    private LocalDateTime dateOpened;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Account.AccountStatus status;
    private String notes;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public String getTruelayerAccountNumber() {
        return truelayerAccountNumber;
    }

    public void setTruelayerAccountNumber(String truelayerAccountNumber) {
        this.truelayerAccountNumber = truelayerAccountNumber;
    }

    public String getTruelayerAccountType() {
        return truelayerAccountType;
    }

    public void setTruelayerAccountType(String truelayerAccountType) {
        this.truelayerAccountType = truelayerAccountType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Long getPercentageOwnership() {
        return percentageOwnership;
    }

    public void setPercentageOwnership(Long percentageOwnership) {
        this.percentageOwnership = percentageOwnership;
    }

    public Boolean getFixedTerm() {
        return fixedTerm;
    }

    public void setFixedTerm(Boolean fixedTerm) {
        this.fixedTerm = fixedTerm;
    }

    public LocalDateTime getFixedTermEndDate() {
        return fixedTermEndDate;
    }

    public void setFixedTermEndDate(LocalDateTime fixedTermEndDate) {
        this.fixedTermEndDate = fixedTermEndDate;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDateTime dateOpened) {
        this.dateOpened = dateOpened;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Account.AccountStatus getStatus() {
        return status;
    }

    public void setStatus(Account.AccountStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}