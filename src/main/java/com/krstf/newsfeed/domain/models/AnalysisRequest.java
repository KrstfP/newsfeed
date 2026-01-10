package com.krstf.newsfeed.domain.models;

import java.time.Instant;
import java.util.UUID;

public class AnalysisRequest {
    private final UUID id;
    private final UUID articleId;
    private AnalysisStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public AnalysisRequest(UUID articleId) {
        this.id = UUID.randomUUID();
        this.articleId = articleId;
        this.status = AnalysisStatus.PENDING;
        this.version = 0L;
    }

    public AnalysisRequest(UUID id, UUID articleId, AnalysisStatus status, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.articleId = articleId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public void start() {
        if (this.status != AnalysisStatus.PENDING) {
            throw new IllegalStateException("Cannot start analysis request that is not in PENDING status.");
        }
        this.status = AnalysisStatus.IN_PROGRESS;
    }

    public void complete() {
        if (this.status != AnalysisStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete analysis request that is not in IN_PROGRESS status.");
        }
        this.status = AnalysisStatus.COMPLETED;
    }

    public void fail() {
        if (this.status != AnalysisStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot fail analysis request that is not in IN_PROGRESS status.");
        }
        this.status = AnalysisStatus.FAILED;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public AnalysisStatus getStatus() {
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
