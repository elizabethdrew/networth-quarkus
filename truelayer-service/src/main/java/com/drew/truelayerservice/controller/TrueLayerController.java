package com.drew.truelayerservice.controller;

import com.drew.truelayerservice.dto.AccountDto;
import com.drew.truelayerservice.dto.TokenResponse;
import com.drew.truelayerservice.service.TrueLayerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@ApplicationScoped
@Path("api/v1/bank")
@Produces(MediaType.APPLICATION_JSON)
public class TrueLayerController {

    private final TrueLayerService trueLayerService;

    public TrueLayerController(TrueLayerService trueLayerService) {
        this.trueLayerService = trueLayerService;
    }

    @GET
    @Path("/add")
    public Response authenticate(@HeaderParam("X-User-ID") String keycloakUserId) {
        String redirectUrl = trueLayerService.authenticateNewBank(keycloakUserId);
        return Response.seeOther(URI.create(redirectUrl)).build();
    }

    @GET
    @Path("/callback")
    public Response callback(@QueryParam("code") String code,
                             @QueryParam("scope") String scope,
                             @QueryParam("state") String state) {
        TokenResponse tokenResponse = trueLayerService.exchangeCodeForToken(code, state);
        return Response.ok(tokenResponse).build();
    }

    @GET
    @Path("/accounts")
    public Response getAccounts(@HeaderParam("X-User-ID") String keycloakUserId) {
        List<AccountDto> accounts = trueLayerService.fetchAccounts(keycloakUserId);
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/update")
    public Response updateBankData(@HeaderParam("X-User-ID") String keycloakUserId) {
        trueLayerService.updateAccounts(keycloakUserId);
        return Response.ok("Update process initiated.").build();
    }
}
