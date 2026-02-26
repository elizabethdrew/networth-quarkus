package com.drew.isaservice.functions;

import com.drew.commonlibrary.dto.AccountIsaDto;
import com.drew.commonlibrary.dto.KafkaBalanceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.drew.isaservice.service.IsaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class IsaFunctions {
    private final IsaService isaService;
    private final ObjectMapper objectMapper;

    @Inject
    public IsaFunctions(IsaService isaService, ObjectMapper objectMapper) {
        this.isaService = isaService;
        this.objectMapper = objectMapper;
    }

    @Incoming("new-isa-account-topic")
    public void addIsaAccount(String payload) throws JsonProcessingException {
        AccountIsaDto accountIsaDto = objectMapper.readValue(payload, AccountIsaDto.class);
        isaService.addIsaAccountToIsaService(accountIsaDto);
    }

    @Incoming("new-balance-topic")
    public void addIsaBalance(String payload) throws JsonProcessingException {
        KafkaBalanceDto kafkaBalanceDto = objectMapper.readValue(payload, KafkaBalanceDto.class);
        isaService.addIsaBalance(kafkaBalanceDto);
    }
}
