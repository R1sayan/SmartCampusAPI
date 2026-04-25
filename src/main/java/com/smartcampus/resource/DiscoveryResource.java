package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> response = new HashMap<>();
        
        // API Version Info
        response.put("name", "Smart Campus API");
        response.put("version", "1.0");
        response.put("student", "Rasayan");
        response.put("module", "5COSC022W - Client Server Architectures");
        response.put("contact", "rasayan@email.com");
        
        // Resource Links (HATEOAS)
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("resources", links);
        
        // Status
        response.put("status", "running");
        
        return Response.ok(response).build();
    }
}