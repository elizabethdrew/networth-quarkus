package com.drew.gatewayserver.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KeycloakProperties {

    @ConfigProperty(name = "gateway.keycloak.introspection-uri", defaultValue = "")
    String introspectionUri;

    @ConfigProperty(name = "gateway.keycloak.client-id", defaultValue = "")
    String clientId;

    @ConfigProperty(name = "gateway.keycloak.client-secret", defaultValue = "")
    String clientSecret;

    public String getIntrospectionUri() {
        return introspectionUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
