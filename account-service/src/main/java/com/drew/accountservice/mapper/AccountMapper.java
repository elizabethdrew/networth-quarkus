package com.drew.accountservice.mapper;

import com.drew.accountservice.dto.AccountInputDto;
import com.drew.accountservice.dto.AccountOutputDto;
import com.drew.accountservice.dto.AccountUpdateDto;
import com.drew.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface AccountMapper {

    Account toEntity(String keycloakUserId, AccountInputDto dto);
    AccountOutputDto toOutputDto(Account account);
    void updateAccount(AccountUpdateDto input, @MappingTarget Account account);
}
