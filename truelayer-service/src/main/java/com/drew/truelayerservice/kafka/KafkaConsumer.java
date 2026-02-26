package com.drew.truelayerservice.kafka;

import com.drew.commonlibrary.dto.RequestAccountBalanceDto;
import com.drew.truelayerservice.service.TrueLayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaConsumer {

    private static final Logger LOG = Logger.getLogger(KafkaConsumer.class);

    private final TrueLayerService trueLayerService;
    private final ObjectMapper objectMapper;

    @Inject
    public KafkaConsumer(TrueLayerService trueLayerService, ObjectMapper objectMapper) {
        this.trueLayerService = trueLayerService;
        this.objectMapper = objectMapper;
    }

    @Incoming("request-account-balance-update-topic")
    public void requestAccountBalanceFromTruelayer(String payload) throws JsonProcessingException {
        RequestAccountBalanceDto requestAccountBalanceDto = objectMapper.readValue(payload, RequestAccountBalanceDto.class);
        LOG.infof("Received update request for account balance accountId=%s", requestAccountBalanceDto.accountId());
        trueLayerService.updateAccountBalanceFromTruelayer(requestAccountBalanceDto);
    }
}
