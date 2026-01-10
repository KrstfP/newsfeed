package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "analysis")
public class AnalysisEntity {
    @Id
    private String id;

    @Indexed
    private final String articleId;

    private final String analysisResult;

    @CreatedDate
    private Instant createdAt;

    public String getAnalysisResult() {
        return analysisResult;
    }

    public AnalysisEntity(String articleId, String analysisResult) {
        this.articleId = articleId;
        this.analysisResult = analysisResult;
    }
}
