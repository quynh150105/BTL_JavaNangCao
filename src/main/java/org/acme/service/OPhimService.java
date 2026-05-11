package org.acme.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.Exception.MovieException;
import org.acme.client.OPhimClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class OPhimService {
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*$");
    private static final Pattern YEAR_PATTERN = Pattern.compile("^\\d{4}$");

    private final OPhimClient gateway;

    @Inject
    public OPhimService(OPhimClient gateway) {
        this.gateway = gateway;
    }

    public JsonNode home() {
        return gateway.get("/v1/api/home", Map.of());
    }

    public JsonNode listByFilter(
            String slug,
            Integer page,
            Integer limit,
            String sortField,
            String sortType,
            String category,
            String country,
            String year
    ) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/danh-sach/" + slug, movieListQuery(page, limit, sortField, sortType, category, country, year));
    }

    public JsonNode movieDetail(String slug) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/phim/" + slug, Map.of());
    }

    public JsonNode movieImages(String slug) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/phim/" + slug + "/images", Map.of());
    }

    public JsonNode movieKeywords(String slug) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/phim/" + slug + "/keywords", Map.of());
    }

    public JsonNode moviePeoples(String slug) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/phim/" + slug + "/peoples", Map.of());
    }

    public JsonNode search(String keyword, Integer page, Integer limit) {
        if (keyword == null || keyword.trim().length() < 2) {
            throw new MovieException(400,"keyword must contain at least 2 characters");
        }
        return gateway.get("/v1/api/tim-kiem", queryWithPaging(page, limit, Map.of("keyword", keyword.trim())));
    }

    public JsonNode categories() {
        return gateway.get("/v1/api/the-loai", Map.of());
    }

    public JsonNode moviesByCategory(
            String slug,
            Integer page,
            Integer limit,
            String sortField,
            String sortType,
            String country,
            String year
    ) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/the-loai/" + slug, movieListQuery(page, limit, sortField, sortType, null, country, year));
    }

    public JsonNode countries() {
        return gateway.get("/v1/api/quoc-gia", Map.of());
    }

    public JsonNode moviesByCountry(
            String slug,
            Integer page,
            Integer limit,
            String sortField,
            String sortType,
            String category,
            String year
    ) {
        validateSlug(slug, "slug");
        return gateway.get("/v1/api/quoc-gia/" + slug, movieListQuery(page, limit, sortField, sortType, category, null, year));
    }

    public JsonNode years() {
        return gateway.get("/v1/api/nam-phat-hanh", Map.of());
    }

    public JsonNode moviesByYear(
            String year,
            Integer page,
            Integer limit,
            String sortField,
            String sortType,
            String category,
            String country
    ) {
        validateYear(year);
        return gateway.get("/v1/api/nam-phat-hanh/" + year, movieListQuery(page, limit, sortField, sortType, category, country, null));
    }

    private Map<String, String> movieListQuery(
            Integer page,
            Integer limit,
            String sortField,
            String sortType,
            String category,
            String country,
            String year
    ) {
        Map<String, String> query = queryWithPaging(page, limit, Map.of());
        putIfPresent(query, "sort_field", sortField);
        putIfPresent(query, "sort_type", sortType);
        putIfPresent(query, "category", category);
        putIfPresent(query, "country", country);
        putIfPresent(query, "year", year);
        return query;
    }

    private Map<String, String> queryWithPaging(Integer page, Integer limit, Map<String, String> base) {
        validateRange(page, "page", 1, 10_000);
        validateRange(limit, "limit", 1, 100);

        Map<String, String> query = new LinkedHashMap<>(base);
        query.put("page", page.toString());
        query.put("limit", limit.toString());
        return query;
    }

    private void validateSlug(String slug, String name) {
        if (slug == null || !SLUG_PATTERN.matcher(slug).matches()) {
            throw new MovieException(400, name + " is invalid");
        }
    }

    private void validateYear(String year) {
        if (year == null || !YEAR_PATTERN.matcher(year).matches()) {
            throw new MovieException(400, "year must contain 4 digits");
        }
    }

    private void validateRange(Integer value, String name, int min, int max) {
        if (value == null || value < min || value > max) {
            throw new MovieException(400, name + " must be between " + min + " and " + max);
        }
    }

    private void putIfPresent(Map<String, String> query, String key, String value) {
        if (value != null && !value.isBlank()) {
            query.put(key, value);
        }
    }
}
