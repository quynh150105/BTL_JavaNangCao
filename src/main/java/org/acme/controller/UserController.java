package org.acme.controller;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ApiResponse;
import org.acme.dto.request.UpdateUserRequest;
import org.acme.dto.response.UserResponse;
import org.acme.service.UserService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Users Resource", description = "Users RESTFull APIs")
@Path("/users")
public class UserController {

    @Inject
    private UserService userService;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @Operation(summary = "Api get All Users")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "Get All User Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            ),
            @APIResponse(
                    responseCode = "404", description = "Not Found"
            )
    }
    )
    public Response getAll() {
        return Response.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .status(Response.Status.OK.getStatusCode())
                        .message("Get List User Success")
                        .data(userService.getAll())
                        .build()
        ).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Authenticated
    @Operation(summary = "Api Get User By Id")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "Get User By Id Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            ),
            @APIResponse(
                    responseCode = "404", description = "Not Found"
            )
    })
    public Response getUserById(
            @Parameter(example = "1", description = "user's Id", required = true)
            @PathParam("id") Long id){
        return Response.ok(
                ApiResponse.<UserResponse>builder()
                        .status(Response.Status.OK.getStatusCode())
                        .data(userService.getById(id))
                        .message("Success")
                        .build()
        ).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Authenticated
    @Operation(summary = "Api Delete User By Id")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "Delete User By Id Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            ),
            @APIResponse(
                    responseCode = "404", description = "Not Found"
            )
    })
    public Response deleteUserById(
            @Parameter(example = "1", description = "user's Id", required = true)
            @PathParam("id") Long id){
        return Response.ok(
                ApiResponse.<UserResponse>builder()
                        .status(Response.Status.OK.getStatusCode())
                        .data(userService.deleteById(id))
                        .message("Delete Success")
                        .build()
        ).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Authenticated
    @Operation(summary = "Api Update User By Id")
    @APIResponses({
            @APIResponse(
                    responseCode = "200", description = "Update User By Id Successful"
            ),
            @APIResponse(
                    responseCode = "400", description = "Bad Request"
            ),
            @APIResponse(
                    responseCode = "404", description = "User Not Found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public Response updateUserById(
            @Parameter(example = "1", description = "user's Id", required = true)
            @PathParam("id") Long id,
            @Valid @RequestBody(description = "New Info of User", required = true) UpdateUserRequest request){
        return Response.ok(
                ApiResponse.<UserResponse>builder()
                        .status(Response.Status.OK.getStatusCode())
                        .data(userService.updateUser(id, request))
                        .message("Delete Success")
                        .build()
        ).build();
    }

}
