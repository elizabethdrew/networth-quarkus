package com.drew.configserver.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigController {

    @GET
    public Response root() {
        return Response.ok("{\"service\":\"configserver\",\"status\":\"running\"}").build();
    }
}
