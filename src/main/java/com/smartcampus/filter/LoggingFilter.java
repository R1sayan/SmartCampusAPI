package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class LoggingFilter 
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = 
            Logger.getLogger(LoggingFilter.class.getName());

    // Log every incoming request
    @Override
    public void filter(ContainerRequestContext requestContext) 
            throws IOException {
        LOGGER.info("====================================");
        LOGGER.info("INCOMING REQUEST:");
        LOGGER.info("Method  : " + requestContext.getMethod());
        LOGGER.info("URI     : " + requestContext.getUriInfo()
                .getRequestUri().toString());
        LOGGER.info("====================================");
    }

    // Log every outgoing response
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) 
            throws IOException {
        LOGGER.info("====================================");
        LOGGER.info("OUTGOING RESPONSE:");
        LOGGER.info("Status  : " + responseContext.getStatus());
        LOGGER.info("====================================");
    }
}