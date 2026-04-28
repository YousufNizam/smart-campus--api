package uk.ac.westminster.smartcampus.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        packages("uk.ac.westminster.smartcampus.api.resource",
                "uk.ac.westminster.smartcampus.api.exception");
        register(JacksonFeature.class);
    }
}
