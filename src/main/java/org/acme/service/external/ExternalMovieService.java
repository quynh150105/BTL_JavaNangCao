package org.acme.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.external.ExternalMovieDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ExternalMovieService {

    private static final Logger LOG = Logger.getLogger(ExternalMovieService.class);

    @ConfigProperty(name = "external.api.base-url")
    private String BASE_URL;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<ExternalMovieDTO> getMovies(int page) {
        try {
            Client client = ClientBuilder.newClient();
            String url = BASE_URL + "/danh-sach/phim-moi-cap-nhat?page=" + page;

            String response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);

            ExternalMovieDTO.ExternalListResponse listResponse =
                    objectMapper.readValue(response, ExternalMovieDTO.ExternalListResponse.class);

            return listResponse.getData() != null ? listResponse.getData() : List.of();
        } catch (Exception e) {
            LOG.error("Error fetching movies from external API: " + e.getMessage(), e);
            return List.of();
        }
    }


    public ExternalMovieDTO getMovieDetail(String slug) {
        try {
            Client client = ClientBuilder.newClient();
            String url = BASE_URL + "/phim/" + slug;

            String response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);

            var jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("movie")) {
                ExternalMovieDTO movie = objectMapper.treeToValue(
                        jsonNode.get("movie"),
                        ExternalMovieDTO.class
                );
                return movie;
            }

            return objectMapper.readValue(response, ExternalMovieDTO.class);

        } catch (Exception e) {
            LOG.error("Error fetching movie detail for slug: " + slug + ", error: " + e.getMessage(), e);
            return null;
        }
    }
}
