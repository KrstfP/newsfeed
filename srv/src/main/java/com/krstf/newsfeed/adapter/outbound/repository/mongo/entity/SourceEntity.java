package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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

    @Indexed
    private final String userId;

    // Null pour les documents existants avant la migration → traité comme ACTIVE
    private final String status;

    public SourceEntity(String id, URI rssFeedUrl, String name, String description, String userId, String status) {
        this.id = id;
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.status = status;
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public URI getRssFeedUrl() {
        return rssFeedUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }
}
