package com.drew.truelayerservice.kafka;

import com.drew.commonlibrary.dto.UpdateAccountBalanceDto;
import com.drew.commonlibrary.dto.UpdateAccountDto;
import com.drew.truelayerservice.dto.AccountDto;
import com.drew.truelayerservice.dto.CardDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaService {

    private static final Logger LOG = Logger.getLogger(KafkaService.class);

    private final Emitter<UpdateAccountDto> accountUpdateEmitter;
    private final Emitter<UpdateAccountBalanceDto> accountBalanceUpdateEmitter;

    @Inject
    public KafkaService(@Channel("truelayer-account-update-topic") Emitter<UpdateAccountDto> accountUpdateEmitter,
                        @Channel("truelayer-account-balance-update-topic") Emitter<UpdateAccountBalanceDto> accountBalanceUpdateEmitter) {
        this.accountUpdateEmitter = accountUpdateEmitter;
        this.accountBalanceUpdateEmitter = accountBalanceUpdateEmitter;
    }

    public void updateAccountKafka(AccountDto accountDto, String keycloakUserId) {
        var updateAccountDto = new UpdateAccountDto(
                keycloakUserId,
                accountDto.getAccountNumber().getNumber(),
                accountDto.getAccountId(),
                accountDto.getAccountType(),
                accountDto.getDisplayName(),
                accountDto.getCurrency(),
                accountDto.getProvider().getDisplayName(),
                accountDto.getUpdateTimestamp());
        LOG.infof("Sending updated account accountId=%s", updateAccountDto.accountId());
        accountUpdateEmitter.send(updateAccountDto);
    }

    public void updateAccountKafka(CardDto cardDto, String keycloakUserId) {
        var updateAccountDto = new UpdateAccountDto(
                keycloakUserId,
                cardDto.getPartialCardNumber(),
                cardDto.getAccountId(),
                "CARD",
                cardDto.getDisplayName(),
                cardDto.getCurrency(),
                cardDto.getProvider().getDisplayName(),
                cardDto.getUpdateTimestamp());
        LOG.infof("Sending updated card accountId=%s", updateAccountDto.accountId());
        accountUpdateEmitter.send(updateAccountDto);
    }

    public void updateAccountBalanceKafka(UpdateAccountBalanceDto updateAccountBalanceDto) {
        LOG.infof("Sending updated account balance accountId=%s", updateAccountBalanceDto.accountId());
        accountBalanceUpdateEmitter.send(updateAccountBalanceDto);
    }
}
