package com.drew.gatewayserver.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class FallbackController {

    @GET
    @Path("contactSupport")
    public String contactSupport() {
        return "An error occurred. Please try after some time or contact support team!!!";
    }
}
