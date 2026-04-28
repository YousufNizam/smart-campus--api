package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper extends AbstractApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public javax.ws.rs.core.Response toResponse(Throwable exception) {
        return buildResponse(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred. Please contact the API administrator.");
    }
}
