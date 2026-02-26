package com.drew.truelayerservice.service.impl;

import com.drew.commonlibrary.dto.RequestAccountBalanceDto;
import com.drew.commonlibrary.dto.UpdateAccountBalanceDto;
import com.drew.truelayerservice.dto.AccountBalanceDto;
import com.drew.truelayerservice.dto.AccountBalanceResponseDto;
import com.drew.truelayerservice.dto.AccountDto;
import com.drew.truelayerservice.dto.AccountResponseDto;
import com.drew.truelayerservice.dto.CardBalanceDto;
import com.drew.truelayerservice.dto.CardBalanceResponseDto;
import com.drew.truelayerservice.dto.CardDto;
import com.drew.truelayerservice.dto.CardResponseDto;
import com.drew.truelayerservice.dto.ErrorResponse;
import com.drew.truelayerservice.dto.TokenResponse;
import com.drew.truelayerservice.entity.Token;
import com.drew.truelayerservice.exception.AccessDeniedException;
import com.drew.truelayerservice.exception.AccountNotFoundException;
import com.drew.truelayerservice.exception.ApiException;
import com.drew.truelayerservice.exception.ConnectorOverloadException;
import com.drew.truelayerservice.exception.ConnectorTimeoutException;
import com.drew.truelayerservice.exception.EndpointNotSupportedException;
import com.drew.truelayerservice.exception.InternalServerErrorException;
import com.drew.truelayerservice.exception.InvalidTokenException;
import com.drew.truelayerservice.exception.ProviderErrorException;
import com.drew.truelayerservice.exception.ProviderRateLimitExceededException;
import com.drew.truelayerservice.exception.ProviderRequestLimitExceededException;
import com.drew.truelayerservice.exception.ProviderTimeoutException;
import com.drew.truelayerservice.exception.ScaExceededException;
import com.drew.truelayerservice.exception.UnauthorizedException;
import com.drew.truelayerservice.kafka.KafkaService;
import com.drew.truelayerservice.repository.TokenRepository;
import com.drew.truelayerservice.service.TrueLayerService;
import com.drew.truelayerservice.util.APIHandler;
import com.drew.truelayerservice.util.RedisService;
import com.drew.truelayerservice.util.TrueLayerUtil;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class TrueLayerServiceImpl implements TrueLayerService {

    private static final Logger LOG = Logger.getLogger(TrueLayerServiceImpl.class);

    private final APIHandler apiHandler;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authBaseUri;
    private final String dataBaseUri;
    private final TokenRepository tokenRepository;
    private final RedisService redisService;
    private final TrueLayerUtil trueLayerUtil;
    private final KafkaService kafkaService;

    public TrueLayerServiceImpl(APIHandler apiHandler,
                                @ConfigProperty(name = "truelayer.client-id") String clientId,
                                @ConfigProperty(name = "truelayer.client-secret") String clientSecret,
                                @ConfigProperty(name = "truelayer.redirect-uri") String redirectUri,
                                @ConfigProperty(name = "truelayer.auth-base-uri") String authBaseUri,
                                @ConfigProperty(name = "truelayer.data-base-uri") String dataBaseUri,
                                TokenRepository tokenRepository,
                                RedisService redisService,
                                TrueLayerUtil trueLayerUtil,
                                KafkaService kafkaService) {
        this.apiHandler = apiHandler;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authBaseUri = authBaseUri;
        this.dataBaseUri = dataBaseUri;
        this.tokenRepository = tokenRepository;
        this.trueLayerUtil = trueLayerUtil;
        this.redisService = redisService;
        this.kafkaService = kafkaService;
    }

    @Override
    public String authenticateNewBank(String keycloakUserId) {
        return UriBuilder.fromUri(authBaseUri + "/")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "info accounts balance cards transactions direct_debits standing_orders offline_access")
                .queryParam("state", keycloakUserId)
                .queryParam("providers", "uk-cs-mock uk-ob-all uk-oauth-all")
                .build()
                .toString();
    }

    @Override
    @Transactional
    public TokenResponse exchangeCodeForToken(String code, String state) {
        try {
            TokenResponse tokenResponse = apiHandler.postForm(
                    authBaseUri + "/connect/token",
                    Map.of(
                            "grant_type", "authorization_code",
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "redirect_uri", redirectUri,
                            "code", code,
                            "state", state
                    ),
                    TokenResponse.class
            );

            LOG.infof("Token response received for userId=%s", state);

            Token token = new Token();
            token.setUserId(state);
            token.setRefreshToken(tokenResponse.getRefresh_token());
            token.setCreatedAt(OffsetDateTime.now());
            tokenRepository.save(token);

            redisService.cacheToken(state,
                    tokenResponse.getAccess_token(),
                    Long.parseLong(tokenResponse.getExpires_in()));

            return tokenResponse;
        } catch (APIHandler.ApiClientException e) {
            handleApiException(e);
            throw new ApiException("Error handling API exception", e);
        } catch (Exception e) {
            throw new ApiException("An unexpected error occurred while exchanging code for token.", e);
        }
    }

    @Override
    public List<AccountDto> fetchAccounts(String keycloakUserId) {
        String url = dataBaseUri + "/data/v1/accounts";
        try {
            AccountResponseDto response = apiHandler.get(url, trueLayerUtil.getAccessToken(keycloakUserId), AccountResponseDto.class);
            return Optional.ofNullable(response)
                    .map(AccountResponseDto::getResults)
                    .orElse(Collections.emptyList());
        } catch (APIHandler.ApiClientException e) {
            handleApiException(e);
            throw new ApiException("Error handling API exception", e);
        } catch (Exception e) {
            throw new ApiException("An unexpected error occurred while fetching accounts.", e);
        }
    }

    @Override
    public List<CardDto> fetchCards(String keycloakUserId) {
        String url = dataBaseUri + "/data/v1/cards";
        try {
            CardResponseDto response = apiHandler.get(url, trueLayerUtil.getAccessToken(keycloakUserId), CardResponseDto.class);
            if (response != null) {
                return response.getResults();
            }
            return Collections.emptyList();
        } catch (APIHandler.ApiClientException e) {
            handleApiException(e);
            throw new ApiException("Error handling API exception", e);
        } catch (Exception e) {
            throw new ApiException("An unexpected error occurred while fetching cards.", e);
        }
    }

    @Override
    public void updateAccounts(String keycloakUserId) {
        List<AccountDto> accounts = fetchAccounts(keycloakUserId);
        List<CardDto> cards = fetchCards(keycloakUserId);

        accounts.forEach(account -> kafkaService.updateAccountKafka(account, keycloakUserId));
        cards.forEach(card -> kafkaService.updateAccountKafka(card, keycloakUserId));
    }

    @Override
    public void updateAccountBalanceFromTruelayer(RequestAccountBalanceDto requestAccountBalanceDto) {
        LOG.infof("Starting request account balance for accountId=%s", requestAccountBalanceDto.accountId());

        if ("CARD".equals(requestAccountBalanceDto.accountType())) {
            List<CardBalanceDto> cardBalanceDto = fetchCardBalance(
                    requestAccountBalanceDto.accountId(),
                    requestAccountBalanceDto.keycloakUserId()
            );
            cardBalanceDto.forEach(cardBalance -> {
                UpdateAccountBalanceDto updateAccountBalanceDto = new UpdateAccountBalanceDto(
                        requestAccountBalanceDto.accountId(),
                        requestAccountBalanceDto.keycloakUserId(),
                        cardBalance.getCurrency(),
                        cardBalance.getCurrent(),
                        cardBalance.getUpdateTimestamp()
                );
                kafkaService.updateAccountBalanceKafka(updateAccountBalanceDto);
            });
            return;
        }

        List<AccountBalanceDto> accountBalanceDto = fetchAccountBalance(
                requestAccountBalanceDto.accountId(),
                requestAccountBalanceDto.keycloakUserId()
        );
        accountBalanceDto.forEach(accountBalance -> {
            UpdateAccountBalanceDto updateAccountBalanceDto = new UpdateAccountBalanceDto(
                    requestAccountBalanceDto.accountId(),
                    requestAccountBalanceDto.keycloakUserId(),
                    accountBalance.getCurrency(),
                    accountBalance.getCurrent(),
                    accountBalance.getUpdateTimestamp()
            );
            kafkaService.updateAccountBalanceKafka(updateAccountBalanceDto);
        });
    }

    public List<CardBalanceDto> fetchCardBalance(String accountId, String keycloakUserId) {
        String url = dataBaseUri + "/data/v1/cards/" + accountId + "/balance";
        try {
            CardBalanceResponseDto response = apiHandler.get(url, trueLayerUtil.getAccessToken(keycloakUserId), CardBalanceResponseDto.class);
            if (response != null) {
                return response.getResults();
            }
            return Collections.emptyList();
        } catch (APIHandler.ApiClientException e) {
            handleApiException(e);
            throw new ApiException("Error handling API exception", e);
        } catch (Exception e) {
            throw new ApiException("An unexpected error occurred while fetching card balance.", e);
        }
    }

    public List<AccountBalanceDto> fetchAccountBalance(String accountId, String keycloakUserId) {
        String url = dataBaseUri + "/data/v1/accounts/" + accountId + "/balance";
        try {
            AccountBalanceResponseDto response = apiHandler.get(url, trueLayerUtil.getAccessToken(keycloakUserId), AccountBalanceResponseDto.class);
            if (response != null) {
                return response.getResults();
            }
            return Collections.emptyList();
        } catch (APIHandler.ApiClientException e) {
            handleApiException(e);
            throw new ApiException("Error handling API exception", e);
        } catch (Exception e) {
            throw new ApiException("An unexpected error occurred while fetching account balance.", e);
        }
    }

    private ErrorResponse parseErrorResponse(String responseBody) {
        return new Gson().fromJson(responseBody, ErrorResponse.class);
    }

    private void handleApiException(APIHandler.ApiClientException e) {
        ErrorResponse errorResponse = parseErrorResponse(e.getBody());
        if (errorResponse == null || errorResponse.getError() == null) {
            throw new ApiException("Error calling provider API: status=" + e.getStatus());
        }

        String description = errorResponse.getErrorDescription();
        switch (errorResponse.getError()) {
            case "unauthorized", "unauthorized_client" -> throw new UnauthorizedException(description);
            case "sca_exceeded" -> throw new ScaExceededException(description);
            case "access_denied" -> throw new AccessDeniedException(description);
            case "account_not_found" -> throw new AccountNotFoundException(description);
            case "invalid_request" -> throw new InvalidTokenException(description);
            case "internal_server_error" -> throw new InternalServerErrorException(description);
            case "provider_too_many_requests" -> throw new ProviderRequestLimitExceededException(description);
            case "provider_request_limit_exceeded" -> throw new ProviderRateLimitExceededException(description);
            case "endpoint_not_supported" -> throw new EndpointNotSupportedException(description);
            case "provider_error", "temporarily_unavailable" -> throw new ProviderErrorException(description);
            case "connector_overload" -> throw new ConnectorOverloadException(description);
            case "connector_timeout" -> throw new ConnectorTimeoutException(description);
            case "provider_timeout" -> throw new ProviderTimeoutException(description);
            default -> throw new ApiException("Error exchanging code for token: " + description);
        }
    }
}
