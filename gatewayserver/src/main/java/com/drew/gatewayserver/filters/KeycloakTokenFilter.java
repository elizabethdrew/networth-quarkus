package com.drew.gatewayserver.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class KeycloakTokenFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(KeycloakTokenFilter.class);

    @Override
    @SuppressWarnings("unchecked")
    public void filter(ContainerRequestContext requestContext) {
        List<String> authHeaders = requestContext.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return;
        }

        String authHeader = authHeaders.get(0);
        if (!authHeader.startsWith("Bearer ")) {
            return;
        }

        String uid = extractUidFromToken(authHeader.substring(7));
        if (uid == null) {
            return;
        }

        requestContext.setProperty("x-user-id", uid);
        LOG.infof("Extracted X-User-ID from token: %s", uid);
    }

    private String extractUidFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }

            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            String decodedJson = new String(decodedPayload, StandardCharsets.UTF_8);
            Map<String, Object> claims = new ObjectMapper().readValue(decodedJson, Map.class);
            Object sub = claims.get("sub");
            return sub == null ? null : sub.toString();
        } catch (IllegalArgumentException | IOException e) {
            LOG.debug("Cannot extract uid from token", e);
            return null;
        }
    }
}
