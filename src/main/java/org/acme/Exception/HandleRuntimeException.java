package org.acme.Exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@Provider
public class HandleRuntimeException implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException e) {

        return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .entity(
                        ApiResponse.error(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage())
                )
                .build();
    }
}
