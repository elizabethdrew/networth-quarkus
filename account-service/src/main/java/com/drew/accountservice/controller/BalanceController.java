package com.drew.accountservice.controller;

import com.drew.accountservice.dto.BalanceDto;
import com.drew.accountservice.dto.BalanceHistoryDto;
import com.drew.accountservice.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/accounts/{accountId}/balances")
@Produces(MediaType.APPLICATION_JSON)
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GET
    @Operation(
            summary = "Get Balance History",
            description = "Retrieves the history of balances for a given account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance history retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to view this account's balance history"),
            @ApiResponse(responseCode = "404", description = "Not Found - the account does not exist or does not belong to the user"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response getBalanceHistory(@HeaderParam("X-User-ID") String keycloakUserId,
                                      @PathParam("accountId") Long accountId) {
        BalanceHistoryDto balanceHistory = balanceService.getBalanceHistory(keycloakUserId, accountId);
        return Response.ok(balanceHistory).build();
    }

    @GET
    @Path("/{balanceId}")
    @Operation(
            summary = "Get Balance By ID",
            description = "Retrieves the specified balance entry for a given account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance entry retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to view this account's balance history"),
            @ApiResponse(responseCode = "404", description = "Not Found - the account or balance entry does not exist or does not belong to the user"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response getBalanceById(@HeaderParam("X-User-ID") String keycloakUserId,
                                   @PathParam("accountId") Long accountId,
                                   @PathParam("balanceId") Long balanceId) {
        BalanceDto balance = balanceService.getBalanceById(keycloakUserId, accountId, balanceId);
        return Response.ok(balance).build();
    }
}
