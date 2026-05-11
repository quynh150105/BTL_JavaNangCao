package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.external.ExternalMovieDTO;
import org.acme.dto.external.ExternalEpisodeDTO;
import org.acme.entity.*;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ExternalMovieMapper {

    public Movie ToEntity(ExternalMovieDTO dto, List<Category> categories, List<Country> countries){
        Movie movie = Movie.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .originName(dto.getOriginName())
                .content(dto.getContent())
                .thumbUrl(dto.getThumbUrl())
                .posterUrl(dto.getPosterUrl())
                .year(dto.getYear())
                .quality(dto.getQuality())
                .lang(dto.getLang())
                .episodeCurrent(dto.getEpisodeCurrent())
                .episodeTotal(dto.getEpisodeTotal())
                .type(dto.getType())
                .status(dto.getStatus())
                .categories(categories)
                .countries(countries)
                .episodes(new ArrayList<>())
                .build();
        
        // Map episodes nếu có
        if (dto.getEpisodes() != null && !dto.getEpisodes().isEmpty()) {
            List<Episode> episodes = mapEpisodes(dto.getEpisodes(), movie);
            movie.setEpisodes(episodes);
        }
        
        return movie;
    }

    private List<Episode> mapEpisodes(List<ExternalEpisodeDTO> externalEpisodes, Movie movie) {
        List<Episode> episodes = new ArrayList<>();

        for (ExternalEpisodeDTO extEpisode : externalEpisodes) {
            Episode episode = new Episode();
            episode.setServerName(extEpisode.getServerName());
            episode.setIsAi(extEpisode.getIsAi() != null ? extEpisode.getIsAi() : false);
            episode.setMovie(movie);

            // Map server data
            if (extEpisode.getItems() != null && !extEpisode.getItems().isEmpty()) {
                List<ServerData> serverDataList = new ArrayList<>();
                for (ExternalEpisodeDTO.ExternalServerDataDTO extServer : extEpisode.getItems()) {
                    ServerData serverData = new ServerData();
                    serverData.setName(extServer.getName());
                    serverData.setSlug(extServer.getSlug());
                    serverData.setFilename(extServer.getFilename());
                    serverData.setLinkEmbed(extServer.getLinkEmbed());
                    serverData.setLinkM3u8(extServer.getLinkM3u8());
                    serverDataList.add(serverData);
                }
                episode.setServerData(serverDataList);
            }

            episodes.add(episode);
        }

        return episodes;
    }
}

