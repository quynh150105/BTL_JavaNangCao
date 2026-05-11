package org.acme.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Exception.MovieException;
import org.acme.service.OPhimService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;


@Path("/v1/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "BPhim", description = "🎬 BPhim Movie APIs - Lấy dữ liệu phim từ BPhim upstream")
public class OPhimController {
    private static final Logger LOG = Logger.getLogger(OPhimController.class);

    private final OPhimService service;

    @Inject
    public OPhimController(OPhimService service) {
        this.service = service;
    }


    @GET
    @Path("/home")
    @Operation(
            summary = "🏠 Lấy danh sách phim nổi bật (trang chủ)",
            description = "Trả về danh sách phim nổi bật, phim được cập nhật mới nhất từ OPhim"
    )
    @APIResponse(responseCode = "200", description = "Dữ liệu phim thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode home() {
        try {
            return service.home();
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim home: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/danh-sach/{slug}")
    @Operation(
            summary = "📽️ Lấy danh sách phim theo loại",
            description = "Lấy danh sách phim theo slug (phim-moi, phim-bo, phim-le, etc.) với các filter: paging, sorting, category, country, year"
    )
    @APIResponse(responseCode = "200", description = "Dữ liệu phim thành công")
    @APIResponse(responseCode = "400", description = "Request không hợp lệ")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode listByFilter(
            @Parameter(
                    name = "slug",
                    description = "Slug danh sách: phim-moi, phim-bo, phim-le, phim-hoat-hinh, etc.",
                    example = "phim-moi",
                    required = true
            )
            @PathParam("slug") String slug,

            @Parameter(
                    name = "page",
                    description = "Số trang (mặc định: 1)",
                    example = "1",
                    schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(type = SchemaType.INTEGER, minimum = "1", maximum = "10000")
            )
            @QueryParam("page") @DefaultValue("1") Integer page,

            @Parameter(
                    name = "limit",
                    description = "Số phim trên trang (mặc định: 24, tối đa: 100)",
                    example = "24",
                    schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(type = SchemaType.INTEGER, minimum = "1", maximum = "100")
            )
            @QueryParam("limit") @DefaultValue("24") Integer limit,

            @Parameter(
                    name = "sort_field",
                    description = "Trường sắp xếp: modified.time, year, _id",
                    example = "modified.time"
            )
            @QueryParam("sort_field") String sortField,

            @Parameter(
                    name = "sort_type",
                    description = "Kiểu sắp xếp: asc (tăng) hoặc desc (giảm)",
                    example = "desc"
            )
            @QueryParam("sort_type") String sortType,

            @Parameter(
                    name = "category",
                    description = "Slug thể loại (cách nhau bằng dấu phẩy)",
                    example = "hanh-dong,tam-ly"
            )
            @QueryParam("category") String category,

            @Parameter(
                    name = "country",
                    description = "Slug quốc gia (cách nhau bằng dấu phẩy)",
                    example = "trung-quoc,han-quoc"
            )
            @QueryParam("country") String country,

            @Parameter(
                    name = "year",
                    description = "Năm phát hành",
                    example = "2024"
            )
            @QueryParam("year") String year
    ) {
        try {
            return service.listByFilter(slug, page, limit, sortField, sortType, category, country, year);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim listByFilter: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/phim/{slug}")
    @Operation(
            summary = "🎞️ Lấy chi tiết phim",
            description = "Trả về thông tin chi tiết phim bao gồm: metadata, danh sách tập, server phát, etc."
    )
    @APIResponse(responseCode = "200", description = "Chi tiết phim thành công")
    @APIResponse(responseCode = "400", description = "Slug không hợp lệ")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode movieDetail(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "quanh-ta-la-cac-sao",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            return service.movieDetail(slug);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim movieDetail: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/phim/{slug}/images")
    @Operation(
            summary = "🖼️ Lấy ảnh phim",
            description = "Trả về danh sách ảnh poster và backdrop của phim"
    )
    @APIResponse(responseCode = "200", description = "Ảnh phim thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode movieImages(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "quanh-ta-la-cac-sao",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            return service.movieImages(slug);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim movieImages: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/phim/{slug}/keywords")
    @Operation(
            summary = "🏷️ Lấy từ khóa phim",
            description = "Trả về danh sách từ khóa TMDB của phim"
    )
    @APIResponse(responseCode = "200", description = "Từ khóa phim thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode movieKeywords(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "quanh-ta-la-cac-sao",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            return service.movieKeywords(slug);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim movieKeywords: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/phim/{slug}/peoples")
    @Operation(
            summary = "👥 Lấy danh sách diễn viên & đạo diễn",
            description = "Trả về danh sách diễn viên và đạo diễn của phim"
    )
    @APIResponse(responseCode = "200", description = "Danh sách peoples thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode moviePeoples(
            @Parameter(
                    name = "slug",
                    description = "Slug của phim",
                    example = "quanh-ta-la-cac-sao",
                    required = true
            )
            @PathParam("slug") String slug
    ) {
        try {
            return service.moviePeoples(slug);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim moviePeoples: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }

    @GET
    @Path("/tim-kiem")
    @Operation(
            summary = "🔍 Tìm kiếm phim",
            description = "Tìm kiếm phim theo từ khóa. Từ khóa phải có ít nhất 2 ký tự"
    )
    @APIResponse(responseCode = "200", description = "Kết quả tìm kiếm thành công")
    @APIResponse(responseCode = "400", description = "Keyword hoặc paging không hợp lệ")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode search(
            @Parameter(
                    name = "keyword",
                    description = "Từ khóa tìm kiếm (tối thiểu 2 ký tự)",
                    example = "quanh ta là các sao",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @QueryParam("keyword") String keyword,

            @Parameter(
                    name = "page",
                    description = "Số trang (mặc định: 1)",
                    example = "1"
            )
            @QueryParam("page") @DefaultValue("1") Integer page,

            @Parameter(
                    name = "limit",
                    description = "Số kết quả trên trang (mặc định: 24, tối đa: 100)",
                    example = "24"
            )
            @QueryParam("limit") @DefaultValue("24") Integer limit
    ) {
        try {
            return service.search(keyword, page, limit);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim search: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/the-loai")
    @Operation(
            summary = "📚 Lấy danh sách thể loại",
            description = "Trả về tất cả thể loại phim có trong OPhim"
    )
    @APIResponse(responseCode = "200", description = "Danh sách thể loại thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode categories() {
        try {
            return service.categories();
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim categories: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/the-loai/{slug}")
    @Operation(
            summary = "📂 Lấy phim theo thể loại",
            description = "Trả về danh sách phim của một thể loại cụ thể"
    )
    @APIResponse(responseCode = "200", description = "Danh sách phim thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode moviesByCategory(
            @Parameter(
                    name = "slug",
                    description = "Slug thể loại",
                    example = "hanh-dong",
                    required = true
            )
            @PathParam("slug") String slug,

            @Parameter(name = "page", description = "Số trang (mặc định: 1)")
            @QueryParam("page") @DefaultValue("1") Integer page,

            @Parameter(name = "limit", description = "Số phim trên trang (mặc định: 24)")
            @QueryParam("limit") @DefaultValue("24") Integer limit,

            @Parameter(name = "sort_field", description = "Trường sắp xếp")
            @QueryParam("sort_field") String sortField,

            @Parameter(name = "sort_type", description = "Kiểu sắp xếp (asc/desc)")
            @QueryParam("sort_type") String sortType,

            @Parameter(name = "country", description = "Slug quốc gia")
            @QueryParam("country") String country,

            @Parameter(name = "year", description = "Năm phát hành")
            @QueryParam("year") String year
    ) {
        try {
            return service.moviesByCategory(slug, page, limit, sortField, sortType, country, year);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim moviesByCategory: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/quoc-gia")
    @Operation(
            summary = "🌍 Lấy danh sách quốc gia",
            description = "Trả về tất cả quốc gia có phim trong OPhim"
    )
    @APIResponse(responseCode = "200", description = "Danh sách quốc gia thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode countries() {
        try {
            return service.countries();
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim countries: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/quoc-gia/{slug}")
    @Operation(
            summary = "🎬 Lấy phim theo quốc gia",
            description = "Trả về danh sách phim của một quốc gia cụ thể"
    )
    @APIResponse(responseCode = "200", description = "Danh sách phim thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode moviesByCountry(
            @Parameter(
                    name = "slug",
                    description = "Slug quốc gia",
                    example = "han-quoc",
                    required = true
            )
            @PathParam("slug") String slug,

            @Parameter(name = "page", description = "Số trang (mặc định: 1)")
            @QueryParam("page") @DefaultValue("1") Integer page,

            @Parameter(name = "limit", description = "Số phim trên trang (mặc định: 24)")
            @QueryParam("limit") @DefaultValue("24") Integer limit,

            @Parameter(name = "sort_field", description = "Trường sắp xếp")
            @QueryParam("sort_field") String sortField,

            @Parameter(name = "sort_type", description = "Kiểu sắp xếp (asc/desc)")
            @QueryParam("sort_type") String sortType,

            @Parameter(name = "category", description = "Slug thể loại")
            @QueryParam("category") String category,

            @Parameter(name = "year", description = "Năm phát hành")
            @QueryParam("year") String year
    ) {
        try {
            return service.moviesByCountry(slug, page, limit, sortField, sortType, category, year);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim moviesByCountry: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/nam-phat-hanh")
    @Operation(
            summary = "📅 Lấy danh sách năm phát hành",
            description = "Trả về tất cả các năm có phim trong OPhim"
    )
    @APIResponse(responseCode = "200", description = "Danh sách năm thành công")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode years() {
        try {
            return service.years();
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim years: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }


    @GET
    @Path("/nam-phat-hanh/{year}")
    @Operation(
            summary = "🎞️ Lấy phim theo năm phát hành",
            description = "Trả về danh sách phim được phát hành trong năm cụ thể"
    )
    @APIResponse(responseCode = "200", description = "Danh sách phim thành công")
    @APIResponse(responseCode = "400", description = "Năm hoặc paging không hợp lệ")
    @APIResponse(responseCode = "502", description = "OPhim API không khả dụng")
    public JsonNode moviesByYear(
            @Parameter(
                    name = "year",
                    description = "Năm phát hành (4 chữ số)",
                    example = "2024",
                    required = true
            )
            @PathParam("year") String year,

            @Parameter(name = "page", description = "Số trang (mặc định: 1)")
            @QueryParam("page") @DefaultValue("1") Integer page,

            @Parameter(name = "limit", description = "Số phim trên trang (mặc định: 24)")
            @QueryParam("limit") @DefaultValue("24") Integer limit,

            @Parameter(name = "sort_field", description = "Trường sắp xếp")
            @QueryParam("sort_field") String sortField,

            @Parameter(name = "sort_type", description = "Kiểu sắp xếp (asc/desc)")
            @QueryParam("sort_type") String sortType,

            @Parameter(name = "category", description = "Slug thể loại")
            @QueryParam("category") String category,

            @Parameter(name = "country", description = "Slug quốc gia")
            @QueryParam("country") String country
    ) {
        try {
            return service.moviesByYear(year, page, limit, sortField, sortType, category, country);
        } catch (MovieException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling OPhim moviesByYear: " + e.getMessage(), e);
            throw new MovieException(Response.Status.BAD_GATEWAY.getStatusCode(), "OPhim upstream error");
        }
    }
}
