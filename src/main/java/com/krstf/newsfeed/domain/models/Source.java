package com.krstf.newsfeed.domain.models;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Source {
    private final UUID id;
    private final URI rssFeedUrl;
    private final String name;
    private final String description;


    public Source(UUID id, URI rssFeedUrl, String name, String description) {
        this.id = id;
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
    }

    public Source(URI rssFeedUrl, String name, String description) {
        this.id = UUID.nameUUIDFromBytes(
                rssFeedUrl.getPath().getBytes(StandardCharsets.UTF_8));
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
    }

    public final UUID getId() { return  id; }
    public final URI getRssFeedUrl() {
        return rssFeedUrl;
    }
    public final String getName() { return name; }
    public String getDescription() { return description; }
}
