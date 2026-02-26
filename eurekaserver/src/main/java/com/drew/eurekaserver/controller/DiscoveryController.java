package com.drew.eurekaserver.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryController {

    @GET
    public Response root() {
        return Response.ok("{\"service\":\"eurekaserver\",\"status\":\"running\"}").build();
    }
}
