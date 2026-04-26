package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        String json = "{"
                + "\"name\":\"Smart Campus API\","
                + "\"version\":\"1.0\","
                + "\"student\":\"Rasayan\","
                + "\"module\":\"5COSC022W - Client Server Architectures\","
                + "\"contact\":\"rasayan@email.com\","
                + "\"status\":\"running\","
                + "\"resources\":{"
                + "\"rooms\":\"/api/v1/rooms\","
                + "\"sensors\":\"/api/v1/sensors\""
                + "}"
                + "}";

        return Response.ok(json).build();
    }
}