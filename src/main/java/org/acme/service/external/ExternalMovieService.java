package org.acme.service.external;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.external.ExternalMovieDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class ExternalMovieService {
    @ConfigProperty(name = "external.api.base-url")
    private String BASE_URL;

    public List<ExternalMovieDTO> getMovies(int page){
        Client client = ClientBuilder.newClient();

        String url = BASE_URL + "/danh-sach/phim-moi-cap-nhat?page=" + page;

        String response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        return List.of();
    }


    public ExternalMovieDTO getMovieDetail(String slug) {
        Client client = ClientBuilder.newClient();

        String url = BASE_URL + "/phim/" + slug;

        String response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        return new ExternalMovieDTO();
    }

}
