package com.networth.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networth.userservice.config.properties.KeycloakProperties;
import com.networth.userservice.dto.KeycloakAccessDto;
import com.networth.userservice.dto.LoginDto;
import com.networth.userservice.dto.LogoutDto;
import com.networth.userservice.dto.RegisterDto;
import com.networth.userservice.dto.TokenResponse;
import com.networth.userservice.dto.UpdateKeycloakDto;
import com.networth.userservice.dto.UserRepresentationDto;
import com.networth.userservice.exception.AuthenticationServiceException;
import com.networth.userservice.exception.KeycloakException;
import com.networth.userservice.exception.UserServiceException;
import com.networth.userservice.util.KeycloakFormDataBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeycloakService {

    private static final Logger LOG = Logger.getLogger(KeycloakService.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakFormDataBuilder keycloakFormDataBuilder;

    public KeycloakService(ObjectMapper objectMapper,
                           KeycloakProperties keycloakProperties,
                           KeycloakFormDataBuilder keycloakFormDataBuilder) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.keycloakProperties = keycloakProperties;
        this.keycloakFormDataBuilder = keycloakFormDataBuilder;
    }

    public String getAdminAccessToken() {
        try {
            KeycloakAccessDto formData = keycloakFormDataBuilder.buildAdminAccessFormData();
            TokenResponse tokenResponse = postForm(
                    keycloakProperties.getBaseUri() + "/realms/" + keycloakProperties.getKeyAdminRealm() + "/protocol/openid-connect/token",
                    Map.of(
                            "client_id", formData.getClientId(),
                            "grant_type", formData.getGrantType(),
                            "username", formData.getUsername(),
                            "password", formData.getPassword()
                    ),
                    TokenResponse.class
            );
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new AuthenticationServiceException("Error during admin access token retrieval", e);
        }
    }

    public TokenResponse getUserAccessToken(LoginDto loginDto) {
        try {
            KeycloakAccessDto formData = keycloakFormDataBuilder.buildUserAccessFormData(loginDto);
            return postForm(
                    keycloakProperties.getBaseUri() + "/realms/" + keycloakProperties.getKeyUserRealm() + "/protocol/openid-connect/token",
                    Map.of(
                            "client_id", formData.getClientId(),
                            "client_secret", formData.getClientSecret(),
                            "scope", formData.getScope(),
                            "grant_type", formData.getGrantType(),
                            "username", formData.getUsername(),
                            "password", formData.getPassword()
                    ),
                    TokenResponse.class
            );
        } catch (Exception e) {
            throw new AuthenticationServiceException("Error during user access token retrieval", e);
        }
    }

    public String createUser(RegisterDto registerDto) {
        try {
            String accessToken = getAdminAccessToken();
            UserRepresentationDto formData = keycloakFormDataBuilder.createUserRepresentation(registerDto);

            HttpResponse<String> response = postJson(
                    keycloakProperties.getBaseUri() + "/admin/realms/" + keycloakProperties.getKeyUserRealm() + "/users",
                    formData,
                    accessToken
            );

            if (response.statusCode() != 201) {
                throw new KeycloakException("Failed to create user in Keycloak. Status: " + response.statusCode());
            }

            return response.headers().firstValue("Location")
                    .map(URI::create)
                    .map(URI::getPath)
                    .map(path -> path.substring(path.lastIndexOf('/') + 1))
                    .filter(id -> !id.isEmpty())
                    .orElseThrow(() -> new KeycloakException("Location header is missing in response from Keycloak"));
        } catch (KeycloakException e) {
            throw e;
        } catch (Exception e) {
            throw new UserServiceException("Unexpected error during user creation", e);
        }
    }

    public void logoutUser(LogoutDto logoutDto) {
        try {
            KeycloakAccessDto formData = keycloakFormDataBuilder.buildLogoutData(logoutDto);
            HttpResponse<String> response = postFormRaw(
                    keycloakProperties.getBaseUri() + "/realms/" + keycloakProperties.getKeyUserRealm() + "/protocol/openid-connect/logout",
                    Map.of(
                            "client_id", formData.getClientId(),
                            "client_secret", formData.getClientSecret(),
                            "refresh_token", formData.getRefreshToken(),
                            "id_token_hint", formData.getIdTokenHint(),
                            "post_logout_redirect_uri", formData.getPostLogoutRedirectUri()
                    )
            );
            if (response.statusCode() != 204 && response.statusCode() != 200) {
                throw new KeycloakException("User logout failed. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new UserServiceException("Unexpected error during user logout", e);
        }
    }

    public void updateEmailKeycloak(String email, String keycloakId) {
        try {
            String accessToken = getAdminAccessToken();
            UpdateKeycloakDto formData = new UpdateKeycloakDto();
            formData.setEmail(email);

            HttpRequest request = HttpRequest.newBuilder(URI.create(
                            keycloakProperties.getBaseUri() + "/admin/realms/" + keycloakProperties.getKeyUserRealm() + "/users/" + keycloakId))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(formData)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                throw new KeycloakException("Failed to update user in Keycloak. Status: " + response.statusCode());
            }
        } catch (KeycloakException e) {
            throw e;
        } catch (Exception e) {
            throw new UserServiceException("Unexpected error during email update in Keycloak", e);
        }
    }

    public void revokeAccessToken(String accessToken) {
        try {
            KeycloakAccessDto formData = keycloakFormDataBuilder.buildRevokeData(accessToken);
            HttpResponse<String> response = postFormRaw(
                    keycloakProperties.getBaseUri() + "/realms/" + keycloakProperties.getKeyUserRealm() + "/protocol/openid-connect/revoke",
                    Map.of(
                            "client_id", formData.getClientId(),
                            "client_secret", formData.getClientSecret(),
                            "token", formData.getToken()
                    )
            );

            if (response.statusCode() != 204 && response.statusCode() != 200) {
                throw new KeycloakException("Access token revocation failed. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new UserServiceException("Unexpected error during access token revocation", e);
        }
    }

    private HttpResponse<String> postJson(String url, Object body, String bearerToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T postForm(String url, Map<String, String> formData, Class<T> responseClass) throws IOException, InterruptedException {
        HttpResponse<String> response = postFormRaw(url, formData);
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AuthenticationServiceException("Keycloak request failed with status " + response.statusCode(), null);
        }
        return objectMapper.readValue(response.body(), responseClass);
    }

    private HttpResponse<String> postFormRaw(String url, Map<String, String> formData) throws IOException, InterruptedException {
        String body = formData.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue() == null ? "" : e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.debugf("Keycloak response status=%d", response.statusCode());
        return response;
    }
}
