package com.drew.gatewayserver.filters;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION - 10)
public class LoggerFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(LoggerFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.infof("Path of the request received -> %s", requestContext.getUriInfo().getPath());
    }
}
