package org.acme.Exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.acme.dto.ApiResponse;
import org.jboss.logging.Logger;

public class HandleMovieException implements ExceptionMapper<MovieException> {
    private static final Logger LOG = Logger.getLogger(MovieException.class);

    @Override
    public Response toResponse(MovieException exception) {
        if (exception.status() >= 500) {
            LOG.errorv(exception, "Returning OPhim error response: status={0}, message={1}",
                    exception.status(),
                    exception.getMessage());
        } else {
            LOG.warnv("Returning OPhim error response: status={0}, message={1}",
                    exception.status(),
                    exception.getMessage());
        }

        return Response.status(exception.status())
                .entity(ApiResponse.error(exception.getMessage()))
                .build();
    }

}
