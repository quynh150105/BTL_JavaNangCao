package org.acme.Exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.dto.ApiResponse;

import java.util.HashMap;

@Provider
public class HandleNotFoundException implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()))
                .build();
    }
}
