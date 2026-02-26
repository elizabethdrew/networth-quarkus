package com.drew.accountservice.controller;

import com.drew.accountservice.dto.NetworthOutputDto;
import com.drew.accountservice.service.NetworthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("api/v1/networth")
@Produces(MediaType.APPLICATION_JSON)
public class NetworthController {

    private final NetworthService networthService;

    public NetworthController(NetworthService networthService) {
        this.networthService = networthService;
    }

    @GET
    @Operation(
            summary = "Get Users Networth",
            description = "Retrieves networth value of the User including liabilities, assets and total."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Networth retrieved successfully", content = @Content(schema = @Schema(implementation = NetworthOutputDto.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient Permissions - the requester does not have permission to view the networth"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - an unexpected error occurred while processing the request")
    })
    public Response getUserNetworth(@HeaderParam("X-User-ID") String keycloakUserId) {
        NetworthOutputDto networth = networthService.getUserNetworth(keycloakUserId);
        return Response.ok(networth).build();
    }
}
