package com.drew.accountservice.kafka;

import com.drew.commonlibrary.dto.RequestAccountBalanceDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class KafkaService {
    private final Emitter<RequestAccountBalanceDto> requestAccountBalanceEmitter;

    @Inject
    public KafkaService(@Channel("request-account-balance-update-topic") Emitter<RequestAccountBalanceDto> requestAccountBalanceEmitter) {
        this.requestAccountBalanceEmitter = requestAccountBalanceEmitter;
    }

//    // OLD - TO ISA SERVICE
//    public void newAccountKafka(String topic, Account account) {
//        var accountIsaDto = new AccountIsaDto(account.getAccountId(), account.getType(), account.getKeycloakUserId());
//        log.info("Sending to Isa Service - New Isa Account: {}", accountIsaDto);
//        streamBridge.send(topic, accountIsaDto);
//    }

//    // OLD - TO ISA SERVICE
//    public void newBalanceKafka(String topic, Balance balance, String keycloakUserId, AccountType accountType) {
//        var kafkaBalanceDto = new KafkaBalanceDto(
//                balance.getAccountId(),
//                keycloakUserId,
//                accountType,
//                balance.getBalance(),
//                balance.getDepositValue(),
//                balance.getWithdrawalValue()
//        );
//        log.info("Alerting isa service about balance update: " + kafkaBalanceDto);
//        streamBridge.send(topic, kafkaBalanceDto);
//    }

    public void requestAccountBalanceKafka(RequestAccountBalanceDto requestAccountBalanceDto) {
        log.info("Requesting account balance update: {}", requestAccountBalanceDto.accountId());
        requestAccountBalanceEmitter.send(requestAccountBalanceDto);
    }
}
