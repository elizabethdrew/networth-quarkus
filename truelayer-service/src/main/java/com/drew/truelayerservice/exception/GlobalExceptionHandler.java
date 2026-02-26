package com.drew.truelayerservice.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception error) {
        if (error instanceof TrueLayerException tle) {
            return Response.status(tle.getStatus())
                    .entity(Map.of("message", tle.getMessage(), "errorCode", tle.getErrorCode()))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("message", "An unexpected error occurred"))
                .build();
    }
}
