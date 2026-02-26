package com.networth.userservice.util;

import com.networth.userservice.config.properties.KeycloakProperties;
import com.networth.userservice.dto.KeycloakAccessDto;
import com.networth.userservice.dto.LoginDto;
import com.networth.userservice.dto.LogoutDto;
import com.networth.userservice.dto.PasswordCredentialDto;
import com.networth.userservice.dto.RegisterDto;
import com.networth.userservice.dto.UserRepresentationDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;

@ApplicationScoped
public class KeycloakFormDataBuilder {

    private final KeycloakProperties keycloakProperties;

    public KeycloakFormDataBuilder(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    public KeycloakAccessDto buildUserAccessFormData(LoginDto loginDto) {
        KeycloakAccessDto formData = new KeycloakAccessDto();
        formData.setClientId(keycloakProperties.getKeyUserClientId());
        formData.setClientSecret(keycloakProperties.getKeyUserClientSecret());
        formData.setScope("openid email profile");
        formData.setGrantType("password");
        formData.setUsername(loginDto.getUsername());
        formData.setPassword(loginDto.getPassword());
        return formData;
    }

    public KeycloakAccessDto buildAdminAccessFormData() {
        KeycloakAccessDto formData = new KeycloakAccessDto();
        formData.setClientId(keycloakProperties.getKeyAdminClientId());
        formData.setGrantType("password");
        formData.setUsername(keycloakProperties.getKeyAdminUsername());
        formData.setPassword(keycloakProperties.getKeyAdminPassword());
        return formData;
    }

    public UserRepresentationDto createUserRepresentation(RegisterDto registerDto) {
        UserRepresentationDto formData = new UserRepresentationDto();
        formData.setUsername(registerDto.getUsername());
        formData.setEmail(registerDto.getEmail());
        formData.setEnabled(true);

        PasswordCredentialDto password = new PasswordCredentialDto();
        password.setType("PASSWORD");
        password.setValue(registerDto.getPassword());
        password.setTemporary(false);

        formData.setCredentials(Collections.singletonList(password));
        return formData;
    }

    public KeycloakAccessDto buildRevokeData(String accessToken) {
        KeycloakAccessDto revokeData = new KeycloakAccessDto();
        revokeData.setClientId(keycloakProperties.getKeyUserClientId());
        revokeData.setClientSecret(keycloakProperties.getKeyUserClientSecret());
        revokeData.setToken(accessToken);
        return revokeData;
    }

    public KeycloakAccessDto buildLogoutData(LogoutDto logoutDto) {
        KeycloakAccessDto formData = new KeycloakAccessDto();
        formData.setClientId(keycloakProperties.getKeyUserClientId());
        formData.setClientSecret(keycloakProperties.getKeyUserClientSecret());
        formData.setRefreshToken(logoutDto.getRefreshToken());
        formData.setIdTokenHint(logoutDto.getIdTokenHint());
        formData.setPostLogoutRedirectUri(keycloakProperties.getLogoutRedirectUrl());
        return formData;
    }
}
