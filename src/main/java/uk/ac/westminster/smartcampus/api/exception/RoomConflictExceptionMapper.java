package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomConflictExceptionMapper extends AbstractApiExceptionMapper
        implements ExceptionMapper<RoomConflictException> {

    @Override
    public Response toResponse(RoomConflictException exception) {
        return buildResponse(Response.Status.CONFLICT, exception.getMessage());
    }
}
