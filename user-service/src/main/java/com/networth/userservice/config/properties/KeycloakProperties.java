package com.networth.userservice.config.properties;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KeycloakProperties {

    @ConfigProperty(name = "keycloak.base-uri")
    String baseUri;

    @ConfigProperty(name = "keycloak.logout-redirect-url")
    String logoutRedirectUrl;

    @ConfigProperty(name = "keycloak.key-admin.realm")
    String keyAdminRealm;

    @ConfigProperty(name = "keycloak.key-admin.client-id")
    String keyAdminClientId;

    @ConfigProperty(name = "keycloak.key-admin.username")
    String keyAdminUsername;

    @ConfigProperty(name = "keycloak.key-admin.password")
    String keyAdminPassword;

    @ConfigProperty(name = "keycloak.key-user.realm")
    String keyUserRealm;

    @ConfigProperty(name = "keycloak.key-user.client-id")
    String keyUserClientId;

    @ConfigProperty(name = "keycloak.key-user.client-secret", defaultValue = "")
    String keyUserClientSecret;

    public String getBaseUri() { return baseUri; }
    public String getLogoutRedirectUrl() { return logoutRedirectUrl; }
    public String getKeyAdminRealm() { return keyAdminRealm; }
    public String getKeyAdminClientId() { return keyAdminClientId; }
    public String getKeyAdminUsername() { return keyAdminUsername; }
    public String getKeyAdminPassword() { return keyAdminPassword; }
    public String getKeyUserRealm() { return keyUserRealm; }
    public String getKeyUserClientId() { return keyUserClientId; }
    public String getKeyUserClientSecret() { return keyUserClientSecret; }
}
