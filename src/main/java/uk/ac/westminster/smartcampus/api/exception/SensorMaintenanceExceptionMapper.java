package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorMaintenanceExceptionMapper extends AbstractApiExceptionMapper
        implements ExceptionMapper<SensorMaintenanceException> {

    @Override
    public Response toResponse(SensorMaintenanceException exception) {
        return buildResponse(Response.Status.FORBIDDEN, exception.getMessage());
    }
}
