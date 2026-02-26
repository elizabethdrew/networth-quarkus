package com.drew.truelayerservice.util;

import com.drew.truelayerservice.dto.TokenResponse;
import com.drew.truelayerservice.entity.Token;
import com.drew.truelayerservice.exception.UnauthorizedException;
import com.drew.truelayerservice.repository.TokenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.OffsetDateTime;
import java.util.Map;

@ApplicationScoped
public class TrueLayerUtil {

    private final APIHandler apiHandler;
    private final String clientId;
    private final String clientSecret;
    private final String authBaseUri;
    private final TokenRepository tokenRepository;
    private final RedisService redisService;

    public TrueLayerUtil(APIHandler apiHandler,
                         @ConfigProperty(name = "truelayer.client-id") String clientId,
                         @ConfigProperty(name = "truelayer.client-secret") String clientSecret,
                         @ConfigProperty(name = "truelayer.auth-base-uri") String authBaseUri,
                         TokenRepository tokenRepository,
                         RedisService redisService) {
        this.apiHandler = apiHandler;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authBaseUri = authBaseUri;
        this.tokenRepository = tokenRepository;
        this.redisService = redisService;
    }

    public String getAccessToken(String keycloakUserId) {
        String accessToken = redisService.getCachedToken(keycloakUserId);
        if (accessToken != null) {
            return accessToken;
        }

        Token refreshTokenEntry = tokenRepository.findByUserId(keycloakUserId)
                .orElseThrow(() -> new UnauthorizedException("No refresh token available - please authenticate"));

        return refreshAccessToken(refreshTokenEntry);
    }

    private String refreshAccessToken(Token refreshTokenEntry) {
        TokenResponse tokenResponse = apiHandler.postForm(
                authBaseUri + "/connect/token",
                Map.of(
                        "grant_type", "refresh_token",
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "refresh_token", refreshTokenEntry.getRefreshToken()
                ),
                TokenResponse.class
        );

        refreshTokenEntry.setRefreshToken(tokenResponse.getRefresh_token());
        refreshTokenEntry.setRefreshedAt(OffsetDateTime.now());
        tokenRepository.save(refreshTokenEntry);

        redisService.cacheToken(refreshTokenEntry.getUserId(),
                tokenResponse.getAccess_token(),
                Long.parseLong(tokenResponse.getExpires_in()));

        return tokenResponse.getAccess_token();
    }
}
