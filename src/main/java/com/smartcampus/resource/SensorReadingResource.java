package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Shared in-memory storage for readings
    public static Map<String, List<SensorReading>> readings = new HashMap<>();

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    // Get all readings for a sensor
    @GET
    public Response getReadings() {
        // Check sensor exists
        Sensor sensor = SensorResource.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: "
                            + sensorId + "\"}")
                    .build();
        }

        // Get readings for this sensor
        List<SensorReading> sensorReadings =
                readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(sensorReadings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    // Add a new reading for a sensor
    @POST
    public Response addReading(SensorReading reading) {
        // Check sensor exists
        Sensor sensor = SensorResource.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: "
                            + sensorId + "\"}")
                    .build();
        }

        // Check sensor is not in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId +
                " is currently under MAINTENANCE and cannot accept readings.");
        }

        // Generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save reading
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // Update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}