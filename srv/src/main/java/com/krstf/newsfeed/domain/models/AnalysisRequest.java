package com.krstf.newsfeed.domain.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a request to analyze an article. Contains information about the article being analyzed,
 * the status of the analysis, and timestamps for when the request was created and last updated.
 */
public class AnalysisRequest {
    private final UUID id;
    private final UUID articleId;
    private AnalysisRequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;


    public AnalysisRequest(UUID id, UUID articleId, AnalysisRequestStatus status, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.articleId = articleId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public void start() {
        this.status = AnalysisRequestStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = AnalysisRequestStatus.COMPLETED;
    }

    public void fail() {
        this.status = AnalysisRequestStatus.FAILED;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public AnalysisRequestStatus getStatus() {
        return status;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
