package uk.ac.westminster.smartcampus.api.exception;

import uk.ac.westminster.smartcampus.api.model.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;

abstract class AbstractApiExceptionMapper {

    @Context
    protected UriInfo uriInfo;

    @Context 
    protected HttpHeaders httpHeaders;

    protected Response buildResponse(Response.Status status, String message) {
        return buildResponse(status.getStatusCode(), status.getReasonPhrase(), message);
    }

    protected Response buildResponse(int statusCode, String reasonPhrase, String message) {
        String path = uriInfo == null ? "" : uriInfo.getRequestUri().getPath();
        ApiError apiError = new ApiError(
                Instant.now().toString(),
                statusCode,
                reasonPhrase,
                message,
                path
        );
        return Response.status(statusCode).entity(apiError).build();
    }
}
