package com.krstf.newsfeed.domain.models;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Article {
    private final UUID id;
    String title;
    String content;
    String url;
    Date publishedAt;
    Instant fetchedAt;
    Source source;
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

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public Source getSource() {
        return source;
    }

    public List<String> getCategories() {
        return categories;
    }

    public Article(String title, String content, String url, Date publishedAt, Source source) {
        this.id = UUID.nameUUIDFromBytes(
                url.getBytes(StandardCharsets.UTF_8));
        this.title = title;
        this.content = content;
        this.url = url;
        this.fetchedAt = Instant.now();
        this.publishedAt = publishedAt;
        this.source = source;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", publishedAt=" + publishedAt +
                ", fetchedAt=" + fetchedAt +
                ", source=" + source +
                ", categories=" + categories +
                '}';
    }
}
