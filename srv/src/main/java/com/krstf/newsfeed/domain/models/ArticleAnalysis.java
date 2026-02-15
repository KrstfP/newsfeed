package com.krstf.newsfeed.domain.models;

import java.util.UUID;

public class ArticleAnalysis {
    private final UUID id;
    private final UUID articleId;
    private final String analysis;

    public ArticleAnalysis(UUID id, UUID articleId, String analysis) {
        this.id = id;
        this.articleId = articleId;
        this.analysis = analysis;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public UUID getId() {
        return id;
    }

    public String getAnalysis() {
        return analysis;
    }
}
