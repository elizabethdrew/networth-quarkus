package com.drew.accountservice.mapper;

import com.drew.accountservice.dto.BalanceDto;
import com.drew.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface BalanceMapper {

    @Mapping(source = "account.accountId", target = "accountId")
    BalanceDto toBalanceDto(Balance balance);
}
