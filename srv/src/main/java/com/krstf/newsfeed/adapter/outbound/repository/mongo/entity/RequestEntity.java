package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "requests")
public class RequestEntity {
    @Id
    private String id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Version
    private Long version;

    private RequestStatus status = RequestStatus.PENDING;

    protected RequestEntity() {
    }


    public RequestEntity(String articleId, Instant createdAt, Instant updatedAt, RequestStatus status, Long version) {
        this.id = articleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.version = version;
    }

    public RequestEntity(String articleId, RequestStatus status) {
        this.id = articleId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public String getArticleId() {
        return id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public RequestEntity(String articleId) {
        this.id = articleId;
    }
}
