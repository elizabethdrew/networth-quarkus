package com.drew.accountservice.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof InvalidAllocationException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
        if (ex instanceof ResourceNotFoundException
                || ex instanceof AccountNotFoundException
                || ex instanceof BalanceNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
        if (ex instanceof WebApplicationException webApplicationException) {
            return webApplicationException.getResponse();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
    }
}
