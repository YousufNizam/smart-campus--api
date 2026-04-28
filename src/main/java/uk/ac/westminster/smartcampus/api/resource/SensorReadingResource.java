package uk.ac.westminster.smartcampus.api.resource;

import uk.ac.westminster.smartcampus.api.model.SensorReading;
import uk.ac.westminster.smartcampus.api.service.SmartCampusStore;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final SmartCampusStore store = SmartCampusStore.getInstance();
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return store.getReadingsForSensor(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        validateReading(reading);
        SensorReading created = store.addReading(sensorId, reading);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    private void validateReading(SensorReading reading) {
        if (reading == null) {
            throw new BadRequestException("Reading payload is required.");
        }
        if (reading.getValue() == null) {
            throw new BadRequestException("Reading value is required.");
        }
        if (reading.getUnit() == null || reading.getUnit().trim().isEmpty()) {
            throw new BadRequestException("Reading unit is required.");
        }
    }
}
