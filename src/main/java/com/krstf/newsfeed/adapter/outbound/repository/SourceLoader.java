package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.domain.models.Source;
import com.krstf.newsfeed.port.outbound.repository.SourceGetter;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class SourceLoader implements SourceGetter {

    @Override
    public List<Source> getSources() {
        return List.of(
                new Source(URI.create("https://feeds.feedburner.com/ZoneMilitaire"), "Opex 360", "L'actualité de la défense et de la sécurité"),
                new Source(URI.create("https://meta-defense.fr/feed"), "Meta Défense", "Plus que l'actualité Défense"),
                new Source(URI.create("https://warontherocks.com/feed/"), "War on the Rocks", "Analysis, commentary, and research on foreign policy and national security")
        );
    }
}
