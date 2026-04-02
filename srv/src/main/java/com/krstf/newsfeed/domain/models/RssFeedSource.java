package com.krstf.newsfeed.domain.models;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Represents a source of RSS feeds, such as a news website or blog.
 * Each source has a unique identifier, a URL for the RSS feed, a name, and a description.
 */
public class RssFeedSource {
    private final UUID id;
    private final URI rssFeedUrl;
    private final String name;
    private final String description;
    private final String userId;
    private RssFeedSourceStatus status;

    public RssFeedSource(URI rssFeedUrl, String name, String description, String userId) {
        this.id = UUID.nameUUIDFromBytes(
                (userId + rssFeedUrl.toString()).getBytes(StandardCharsets.UTF_8));
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.status = RssFeedSourceStatus.ACTIVE;
    }

    public RssFeedSource(UUID id, URI rssFeedUrl, String name, String description, String userId, RssFeedSourceStatus status) {
        this.id = id;
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.status = status;
    }

    public final UUID getId() {
        return id;
    }

    public final URI getRssFeedUrl() {
        return rssFeedUrl;
    }

    public final String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public RssFeedSourceStatus getStatus() {
        return status;
    }

    public void delete() {
        this.status = RssFeedSourceStatus.DELETED;
    }
}
