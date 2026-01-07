package com.krstf.newsfeed.adapter.outbound.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.util.UUID;

@Document(collection = "sources")
public class SourceEntity {
    @Id
    private final String id;
    private final URI rssFeedUrl;
    private final String name;
    private final String description;

    public SourceEntity(String id, URI rssFeedUrl, String name, String description) {
        this.id = id;
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
    }

    public final UUID getId() { return  UUID.fromString(id); }
    public final URI getRssFeedUrl() {
        return rssFeedUrl;
    }
    public final String getName() { return name; }
    public final String getDescription() { return description; }

}
