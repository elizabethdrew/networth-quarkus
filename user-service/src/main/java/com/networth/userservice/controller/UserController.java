package com.networth.userservice.controller;

import com.networth.userservice.dto.RegisterDto;
import com.networth.userservice.dto.UpdateUserDto;
import com.networth.userservice.dto.UserOutput;
import com.networth.userservice.service.UserService;
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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
@Path("api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserOutput.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request"),
            @ApiResponse(responseCode = "409", description = "Already Exists"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response registerUser(RegisterDto registerDto, @jakarta.ws.rs.core.Context UriInfo uriInfo) {
        UserOutput userOutput = userService.registerUser(registerDto);
        URI location = UriBuilder.fromUri(uriInfo.getAbsolutePath()).path(String.valueOf(userOutput.getUserId())).build();
        return Response.created(location).entity(userOutput).build();
    }

    @GET
    @Operation(summary = "Get a user by Keycloak ID")
    public Response getUser(@HeaderParam("X-User-ID") String keycloakUserId) {
        return Response.ok(userService.getUser(keycloakUserId)).build();
    }

    @PUT
    @Operation(summary = "Update a user by Keycloak ID")
    public Response updateUser(@HeaderParam("X-User-ID") String keycloakUserId, UpdateUserDto updateUserDto) {
        return Response.ok(userService.updateUser(keycloakUserId, updateUserDto)).build();
    }

    @DELETE
    @Operation(summary = "Soft delete a user by Keycloak ID")
    public Response deleteUser(@HeaderParam("X-User-ID") String keycloakUserId) {
        userService.deleteUser(keycloakUserId);
        return Response.noContent().build();
    }
}
