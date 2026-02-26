package com.networth.userservice.exception;

import com.networth.userservice.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof ResourceNotFoundException || e instanceof UserNotFoundException) {
            return buildResponse(404, "Not Found");
        }
        if (e instanceof InvalidInputException || e instanceof InvalidCredentialsException) {
            return buildResponse(400, "Invalid Input");
        }
        if (e instanceof DuplicateException) {
            return buildResponse(409, "Duplicate Input");
        }
        if (e instanceof InsufficientPermissionException) {
            return buildResponse(403, "Insufficient Permissions");
        }
        if (e instanceof KeycloakException) {
            return buildResponse(500, "Keycloak Error");
        }
        if (e instanceof UserServiceException) {
            return buildResponse(500, "User Service Error");
        }
        return buildResponse(500, "Internal Server Error");
    }

    private Response buildResponse(int code, String message) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(code);
        error.setMessage(message);
        return Response.status(code).entity(error).build();
    }
}
