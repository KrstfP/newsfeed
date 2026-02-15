package com.krstf.newsfeed.domain.models;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Article {
    private final UUID id;
    private final String title;
    private final String content;
    private final String url;
    private final Date publishedAt;
    private final UUID sourceId;
    private final String sourceName;

    List<String> categories;

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public List<String> getCategories() {
        return categories;
    }

    public UUID getSourceId() { return sourceId; }

    public String getSourceName() { return sourceName; }

    public Article(String title, String content, String url, Date publishedAt, UUID sourceId, String sourceName) {
        this.id = UUID.nameUUIDFromBytes(
                url.getBytes(StandardCharsets.UTF_8));
        this.title = title;
        this.content = content;
        this.url = url;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
    }

    public Article(UUID id, String title, String content, String url, Date publishedAt, UUID sourceId, String sourceName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
