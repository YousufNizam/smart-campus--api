package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper extends AbstractApiExceptionMapper
        implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        String message = exception.getMessage() == null ? response.getStatusInfo().getReasonPhrase() : exception.getMessage();
        return buildResponse(response.getStatus(), response.getStatusInfo().getReasonPhrase(), message);
    }
}
