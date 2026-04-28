package uk.ac.westminster.smartcampus.api.resource;

import uk.ac.westminster.smartcampus.api.model.DiscoveryResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public DiscoveryResponse discover() {
        Map<String, String> collections = new LinkedHashMap<>();
        collections.put("rooms", "/api/v1/rooms");
        collections.put("sensors", "/api/v1/sensors");
        collections.put("sensorReadingsTemplate", "/api/v1/sensors/{sensorId}/readings");

        return new DiscoveryResponse(
                "SmartCampus Sensor and Room Management API",
                "v1",
                "RESTful JAX-RS service for managing rooms, sensors, and historical sensor readings.",
                "smartcampus-admin@westminster.ac.uk",
                collections
        );
    }
}
