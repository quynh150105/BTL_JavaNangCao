package org.acme.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ApiResponse;
import org.acme.dto.request.CreateUserRequest;
import org.acme.dto.request.LoginRequest;
import org.acme.dto.response.LoginResponse;
import org.acme.dto.response.RegisterResponse;
import org.acme.service.AuthService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Auth Resource", description = "Auth RESTFull API")
public class AuthController {
    @Inject
    private AuthService authService;

    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Register Api")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "register Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            )
        }
    )
    public Response register(@Valid
                             @RequestBody(description = "Thong tin dang ky", required = true) CreateUserRequest request) {
        return Response.ok(
                ApiResponse.<RegisterResponse>builder()
                        .status(Response.Status.CREATED.getStatusCode())
                        .message("register successful")
                        .data(authService.register(request))
                        .build()
        ).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login Api")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "Login Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            )
    })
    public Response login(@Valid
                          @RequestBody(description = "Thong tin dang nhap", required = true) LoginRequest request) {
        return Response.ok(
                ApiResponse.<LoginResponse>builder()
                        .status(Response.Status.ACCEPTED.getStatusCode())
                        .message("Login successful")
                        .data(authService.login(request))
                        .build()
        ).build();
    }
}
