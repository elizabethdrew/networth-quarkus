package com.drew.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BalanceDto(
        Long balanceId,
        Long accountId,
        BigDecimal balance,
        BigDecimal differenceFromLast,
        LocalDateTime dateUpdated
) {
}
