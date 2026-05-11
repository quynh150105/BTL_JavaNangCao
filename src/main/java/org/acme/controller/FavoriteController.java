package org.acme.controller;

import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.ApiResponse;
import org.acme.dto.response.MovieDetailResponse;
import org.acme.dto.response.MovieResponse;
import org.acme.dto.response.UserResponse;
import org.acme.repository.UserRepository;
import org.acme.service.FavoriteService;
import org.acme.service.MovieService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Tag(name = "Movies Resource", description = "Quản lý thông tin danh sách phim yêu thích")
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class FavoriteController {

    @Inject
    private MovieService movieService;

    @Inject
    private FavoriteService favoriteService;

    @Inject
    private JsonWebToken jwt;

    @Inject
    private UserRepository userRepository;


    @GET
    @Path("")
    @Operation(
            summary = "Lấy danh sách phim",
            description = "Trả về danh sách phim có phân trang từ database hoặc external API"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lấy danh sách phim thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Lỗi request"
            )
    })
    @Authenticated
    public Response getMovies(
            @Parameter(
                    name = "page",
                    description = "Số trang",
                    example = "1",
                    schema = @Schema(type = SchemaType.INTEGER, minimum = "1")
            )
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        try {
            List<MovieResponse> movies = movieService.getMovies(page);
            return Response.ok(
                    ApiResponse.<List<MovieResponse>>builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Lấy danh sách phim thành công")
                            .data(movies)
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }


    @GET
    @Path("/detail/{slug}")
    @Authenticated
    @Operation(
            summary = "Lấy chi tiết phim theo slug",
            description = "Trả về thông tin chi tiết phim bao gồm danh sách tập phim"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lấy phim thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Phim không tìm thấy"
            )
    })
    public Response getMovieBySlug(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim (vd: phim-hay)",
                    example = "phim-hay",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            MovieDetailResponse movie = movieService.getMovieBySlug(slug);
            return Response.ok(
                    ApiResponse.<MovieDetailResponse>builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Lấy phim thành công")
                            .data(movie)
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.NOT_FOUND.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    /**
     * Lấy chi tiết phim theo ID
     */
    @GET
    @Path("/{id}")
    @Authenticated
    @Operation(
            summary = "Lấy chi tiết phim theo ID",
            description = "Trả về thông tin chi tiết phim từ database"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lấy phim thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Phim không tìm thấy"
            )
    })
    public Response getMovieById(
            @Parameter(
                    name = "id",
                    description = "ID của phim",
                    example = "1",
                    required = true,
                    schema = @Schema(type = SchemaType.INTEGER)
            )
            @PathParam("id") Long id
    ) {
        try {
            MovieDetailResponse movie = movieService.getMovieById(id);
            return Response.ok(
                    ApiResponse.<MovieDetailResponse>builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Lấy phim thành công")
                            .data(movie)
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.NOT_FOUND.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    /**
     * Thêm phim vào danh sách yêu thích
     */
    @POST
    @Path("/favorites/{slug}")
    @Authenticated
//    @SecurityRequirement(name = "bearer")
    @Operation(
            summary = "Thêm phim vào danh sách yêu thích",
            description = "Thêm một bộ phim vào danh sách yêu thích của user hiện tại (yêu cầu xác thực)"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Thêm vào yêu thích thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Phim hoặc user không tìm thấy"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Không xác thực"
            )
    })
    public Response addFavorite(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "phim-hay",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            String userName = jwt.getSubject();
            UserResponse user = userRepository.find("username",userName)
                    .project(UserResponse.class)
                    .firstResultOptional()
                    .orElseThrow(()-> new NotFoundException("User Not Found"));

            Log.info("User id: " + user.getId());
            favoriteService.addFavorite(user.getId(),  slug);

            return Response.ok(
                    ApiResponse.builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Thêm vào yêu thích thành công")
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    /**
     * Xóa phim khỏi danh sách yêu thích
     */
    @DELETE
    @Path("/favorites/{slug}")
    @Authenticated
//    @SecurityRequirement(name = "bearer")
    @Operation(
            summary = "Xóa phim khỏi danh sách yêu thích",
            description = "Xóa một bộ phim khỏi danh sách yêu thích của user hiện tại (yêu cầu xác thực)"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Xóa khỏi yêu thích thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Phim hoặc yêu thích không tìm thấy"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Không xác thực"
            )
    })
    public Response removeFavorite(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "phim-hay",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            String userName = jwt.getSubject();
            UserResponse user = userRepository.find("username",userName)
                    .project(UserResponse.class)
                    .firstResultOptional()
                    .orElseThrow(()-> new NotFoundException("User Not Found"));

            Log.info("User id: " + user.getId());
            favoriteService.removeFavorite(user.getId(), slug);

            return Response.ok(
                    ApiResponse.builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Xóa khỏi yêu thích thành công")
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    /**
     * Lấy danh sách phim yêu thích của user
     */
    @GET
    @Path("/favorites")
    @Authenticated
//    @SecurityRequirement(name = "bearer")
    @Operation(
            summary = "Lấy danh sách phim yêu thích",
            description = "Trả về danh sách phim yêu thích của user hiện tại (yêu cầu xác thực)"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User không tìm thấy"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Không xác thực"
            )
    })
    public Response getFavorites(
            @Parameter(
                    name = "page",
                    description = "Số trang",
                    example = "1",
                    schema = @Schema(type = SchemaType.INTEGER, minimum = "1")
            )
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        try {
            String userName = jwt.getSubject();
            UserResponse user = userRepository.find("username",userName)
                    .project(UserResponse.class)
                    .firstResultOptional()
                    .orElseThrow(()-> new NotFoundException("User Not Found"));

            Log.info("User id: " + user.getId());
            List<MovieDetailResponse> favorites = favoriteService.getFavorites(user.getId(), page);

            return Response.ok(
                    ApiResponse.<List<MovieDetailResponse>>builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Lấy danh sách yêu thích thành công")
                            .data(favorites)
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    /**
     * Kiểm tra xem phim có trong danh sách yêu thích không
     */
    @GET
    @Path("/favorites/{slug}/check")
    @Authenticated
    @Operation(
            summary = "Kiểm tra phim trong yêu thích",
            description = "Kiểm tra xem một bộ phim có trong danh sách yêu thích của user hiện tại không (yêu cầu xác thực)"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Kiểm tra thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Không xác thực"
            )
    })
    public Response checkFavorite(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "phim-hay",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            String userName = jwt.getSubject();
            UserResponse user = userRepository.find("username",userName)
                    .project(UserResponse.class)
                    .firstResultOptional()
                    .orElseThrow(()-> new NotFoundException("User Not Found"));

            Log.info("User id: " + user.getId());
            boolean isFavorited = favoriteService.isFavorited(user.getId(), slug);

            return Response.ok(
                    ApiResponse.<Boolean>builder()
                            .status(Response.Status.OK.getStatusCode())
                            .message("Kiểm tra thành công")
                            .data(isFavorited)
                            .build()
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("Lỗi: " + e.getMessage())
                            .build())
                    .build();
        }
    }
}
