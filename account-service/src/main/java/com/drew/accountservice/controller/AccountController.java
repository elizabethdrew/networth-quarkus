package com.drew.accountservice.controller;

import com.drew.accountservice.dto.AccountInputDto;
import com.drew.accountservice.dto.AccountOutputDto;
import com.drew.accountservice.dto.AccountUpdateDto;
import com.drew.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@Path("api/v1/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Operation(
            summary = "Register a new account",
            description = "Creates a new account for logged in user with the provided information." +
                    "The account's unique identifier is returned in the Location header of the response.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(schema = @Schema(implementation = AccountOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request - the provided data is invalid or incomplete"),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to create a new account"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response createAccount(@HeaderParam("X-User-ID") String keycloakUserId,
                                  AccountInputDto accountInputDto,
                                  @Context UriInfo uriInfo) {
        AccountOutputDto accountOutputDto = accountService.createAccount(keycloakUserId, accountInputDto);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(accountOutputDto.getAccountId()))
                .build();

        return Response.created(location).entity(accountOutputDto).build();
    }

    @GET
    @Operation(
            summary = "Get User Accounts",
            description = "Retrieves all accounts associated with the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully", content = @Content(schema = @Schema(implementation = AccountOutputDto.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to view the accounts"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response getUserAccounts(@HeaderParam("X-User-ID") String keycloakUserId) {
        List<AccountOutputDto> accounts = accountService.getUserAccounts(keycloakUserId);
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/{accountId}")
    @Operation(
            summary = "Get Account by ID",
            description = "Retrieves the account with the specified ID if it belongs to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully", content = @Content(schema = @Schema(implementation = AccountOutputDto.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to view this account"),
            @ApiResponse(responseCode = "404", description = "Not Found - the account does not exist or does not belong to the user"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response getAccountById(@HeaderParam("X-User-ID") String keycloakUserId, @PathParam("accountId") Long accountId) {
        Optional<AccountOutputDto> accountOutputDto = accountService.getAccountByIdAndKeycloakId(accountId, keycloakUserId);

        return accountOutputDto.map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{accountId}")
    @Operation(
            summary = "Update Account by ID",
            description = "Updates the account with the specified ID if it belongs to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content(schema = @Schema(implementation = AccountOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request - the provided data is invalid or incomplete"),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to update this account"),
            @ApiResponse(responseCode = "404", description = "Not Found - the account does not exist or does not belong to the user"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response updateAccountById(@HeaderParam("X-User-ID") String keycloakUserId,
                                      @PathParam("accountId") Long accountId,
                                      AccountUpdateDto accountUpdateDto) {
        Optional<AccountOutputDto> updatedAccount = accountService.updateAccountByIdAndKeycloakId(accountId, keycloakUserId, accountUpdateDto);

        return updatedAccount.map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{accountId}")
    @Operation(
            summary = "Soft Delete Account by ID",
            description = "Archives the account with the specified ID if it belongs to the logged-in user, marking it as inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account archived successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to archive this account"),
            @ApiResponse(responseCode = "404", description = "Not Found - the account does not exist or does not belong to the user"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response softDeleteAccount(@HeaderParam("X-User-ID") String keycloakUserId, @PathParam("accountId") Long accountId) {
        boolean isDeleted = accountService.softDeleteAccount(accountId, keycloakUserId);
        return isDeleted ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
