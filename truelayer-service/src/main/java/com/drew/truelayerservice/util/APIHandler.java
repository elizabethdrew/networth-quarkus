package com.drew.truelayerservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

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
public class APIHandler {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public APIHandler(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public <R> R get(String apiEndpoint, String bearerToken, Class<R> responseClass) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiEndpoint))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();
        return execute(request, responseClass);
    }

    public <R> R postForm(String apiEndpoint, Map<String, String> form, Class<R> responseClass) {
        String body = form.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create(apiEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return execute(request, responseClass);
    }

    private <R> R execute(HttpRequest request, Class<R> responseClass) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiClientException(response.statusCode(), response.body());
            }
            if (responseClass == String.class) {
                return responseClass.cast(response.body());
            }
            return objectMapper.readValue(response.body(), responseClass);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Interrupted during API call", e);
        } catch (IOException e) {
            throw new ApiException("Failed to parse API response", e);
        }
    }

    public static class ApiClientException extends RuntimeException {
        private final int status;
        private final String body;

        public ApiClientException(int status, String body) {
            super("API call failed with status " + status);
            this.status = status;
            this.body = body;
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }
    }

    public static class ApiException extends RuntimeException {
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
