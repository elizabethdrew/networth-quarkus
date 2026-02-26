package com.drew.gatewayserver.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.WILDCARD)
public class GatewayProxyController {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @ConfigProperty(name = "gateway.routes.user-service-url", defaultValue = "http://localhost:8081")
    String userServiceUrl;

    @ConfigProperty(name = "gateway.routes.account-service-url", defaultValue = "http://localhost:8082")
    String accountServiceUrl;

    @ConfigProperty(name = "gateway.routes.truelayer-service-url", defaultValue = "http://localhost:8084")
    String truelayerServiceUrl;

    @GET
    @Path("/{path: .*}")
    public Response get(@PathParam("path") String path,
                        @Context UriInfo uriInfo,
                        @Context HttpHeaders headers,
                        @Context ContainerRequestContext requestContext) {
        return forward("GET", path, "", uriInfo, headers, requestContext);
    }

    @DELETE
    @Path("/{path: .*}")
    public Response delete(@PathParam("path") String path,
                           @Context UriInfo uriInfo,
                           @Context HttpHeaders headers,
                           @Context ContainerRequestContext requestContext) {
        return forward("DELETE", path, "", uriInfo, headers, requestContext);
    }

    @HEAD
    @Path("/{path: .*}")
    public Response head(@PathParam("path") String path,
                         @Context UriInfo uriInfo,
                         @Context HttpHeaders headers,
                         @Context ContainerRequestContext requestContext) {
        return forward("HEAD", path, "", uriInfo, headers, requestContext);
    }

    @OPTIONS
    @Path("/{path: .*}")
    public Response options(@PathParam("path") String path,
                            @Context UriInfo uriInfo,
                            @Context HttpHeaders headers,
                            @Context ContainerRequestContext requestContext) {
        return forward("OPTIONS", path, "", uriInfo, headers, requestContext);
    }

    @POST
    @Path("/{path: .*}")
    public Response post(@PathParam("path") String path,
                         String body,
                         @Context UriInfo uriInfo,
                         @Context HttpHeaders headers,
                         @Context ContainerRequestContext requestContext) {
        return forward("POST", path, body, uriInfo, headers, requestContext);
    }

    @PUT
    @Path("/{path: .*}")
    public Response put(@PathParam("path") String path,
                        String body,
                        @Context UriInfo uriInfo,
                        @Context HttpHeaders headers,
                        @Context ContainerRequestContext requestContext) {
        return forward("PUT", path, body, uriInfo, headers, requestContext);
    }

    @PATCH
    @Path("/{path: .*}")
    public Response patch(@PathParam("path") String path,
                          String body,
                          @Context UriInfo uriInfo,
                          @Context HttpHeaders headers,
                          @Context ContainerRequestContext requestContext) {
        return forward("PATCH", path, body, uriInfo, headers, requestContext);
    }

    private Response forward(String method,
                             String path,
                             String body,
                             UriInfo uriInfo,
                             HttpHeaders headers,
                             ContainerRequestContext requestContext) {
        String serviceBaseUrl = resolveTargetService(path);
        if (serviceBaseUrl == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "No gateway route configured for path: /api/v1/" + path))
                    .build();
        }

        String query = uriInfo.getRequestUri().getRawQuery();
        String upstreamUrl = serviceBaseUrl + "/api/v1/" + path + (query == null ? "" : "?" + query);

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(upstreamUrl));
        copyHeaders(headers.getRequestHeaders(), builder, requestContext);

        if ("GET".equals(method) || "DELETE".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
        }

        try {
            HttpResponse<String> upstreamResponse = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            Response.ResponseBuilder responseBuilder = Response.status(upstreamResponse.statusCode());
            upstreamResponse.headers().firstValue("content-type").ifPresent(value -> responseBuilder.header("Content-Type", value));
            upstreamResponse.headers().firstValue("location").ifPresent(value -> responseBuilder.header("Location", value));
            return responseBuilder.entity(upstreamResponse.body()).build();
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("message", "Unable to proxy request", "reason", e.getMessage()))
                    .build();
        }
    }

    private void copyHeaders(MultivaluedMap<String, String> inboundHeaders,
                             HttpRequest.Builder builder,
                             ContainerRequestContext requestContext) {
        for (Map.Entry<String, List<String>> entry : inboundHeaders.entrySet()) {
            String name = entry.getKey();
            if (isRestrictedHeader(name)) {
                continue;
            }
            for (String value : entry.getValue()) {
                builder.header(name, value);
            }
        }

        Object extractedUserId = requestContext.getProperty("x-user-id");
        if (extractedUserId != null && inboundHeaders.getFirst("X-User-ID") == null) {
            builder.header("X-User-ID", extractedUserId.toString());
        }
    }

    private boolean isRestrictedHeader(String name) {
        return "host".equalsIgnoreCase(name)
                || "content-length".equalsIgnoreCase(name)
                || "connection".equalsIgnoreCase(name)
                || "expect".equalsIgnoreCase(name)
                || "upgrade".equalsIgnoreCase(name)
                || "transfer-encoding".equalsIgnoreCase(name);
    }

    private String resolveTargetService(String path) {
        if (path.startsWith("users/") || path.equals("users") || path.startsWith("auth/") || path.equals("auth")) {
            return userServiceUrl;
        }
        if (path.startsWith("accounts/") || path.equals("accounts") || path.startsWith("networth/") || path.equals("networth")) {
            return accountServiceUrl;
        }
        if (path.startsWith("bank/") || path.equals("bank")) {
            return truelayerServiceUrl;
        }
        return null;
    }
}
