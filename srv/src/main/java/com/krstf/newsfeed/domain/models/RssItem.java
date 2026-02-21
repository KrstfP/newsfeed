package com.krstf.newsfeed.domain.models;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Represents an individual news article or item from an RSS feed.
 * Each RssItem has a unique identifier, a title, content, a URL, a publication date, and is associated with a source.
 * It may also have a list of categories or tags that describe the content of the article.
 */
public class RssItem {
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

    public UUID getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public RssItem(String title, String content, String url, Date publishedAt, UUID sourceId, String sourceName) {
        this.id = UUID.nameUUIDFromBytes(
                url.getBytes(StandardCharsets.UTF_8));
        this.title = title;
        this.content = content;
        this.url = url;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
    }

    public RssItem(UUID id, String title, String content, String url, Date publishedAt, UUID sourceId, String sourceName) {
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
