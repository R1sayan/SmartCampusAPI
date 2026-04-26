package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;
import java.io.IOException;

public class Main {

    public static final String BASE_URI = "http://0.0.0.0:8080/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages(
                    "com.smartcampus.resource",
                    "com.smartcampus.exception",
                    "com.smartcampus.filter"
                )
                .register(JacksonFeature.class);
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println("=========================================");
        System.out.println(" Smart Campus API - Started Successfully!");
        System.out.println("=========================================");
        System.out.println(" URL : http://localhost:8080/api/v1");
        System.out.println(" Student : Rasayan");
        System.out.println("=========================================");
        System.out.println(" Press ENTER to stop the server...");
        System.in.read();
        server.stop();
        System.out.println(" Server stopped.");
    }
}