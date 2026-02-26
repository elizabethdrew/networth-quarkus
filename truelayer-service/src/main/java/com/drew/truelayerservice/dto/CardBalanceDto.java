package com.drew.truelayerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardBalanceDto {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("available")
    private Long available;

    @JsonProperty("current")
    private Long current;

    @JsonProperty("credit_limit")
    private Long creditLimit;

    @JsonProperty("last_statement_date")
    private String lastStatementDate;

    @JsonProperty("last_statement_balance")
    private Long lastStatementBalance;

    @JsonProperty("payment_due")
    private Long paymentDue;

    @JsonProperty("payment_due_date")
    private String paymentDueDate;

    @JsonProperty("update_timestamp")
    private String updateTimestamp;

}
