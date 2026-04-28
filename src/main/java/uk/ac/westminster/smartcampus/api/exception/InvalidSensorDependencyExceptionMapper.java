package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidSensorDependencyExceptionMapper extends AbstractApiExceptionMapper
        implements ExceptionMapper<InvalidSensorDependencyException> {

    @Override
    public Response toResponse(InvalidSensorDependencyException exception) {
        return buildResponse(422, "Unprocessable Entity", exception.getMessage());
    }
}
