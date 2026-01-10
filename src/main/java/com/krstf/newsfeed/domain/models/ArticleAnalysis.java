package com.krstf.newsfeed.domain.models;

import java.util.UUID;

public class ArticleAnalysis {
    private final UUID id;
    private final String analysis;

    public ArticleAnalysis(UUID id, String analysis) {
        this.id = id;
        this.analysis = analysis;
    }

    public UUID getId() {
        return id;
    }

    public String getAnalysis() {
        return analysis;
    }
}
