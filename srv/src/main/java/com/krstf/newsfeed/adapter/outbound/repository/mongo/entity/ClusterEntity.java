package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "clusters")
public class ClusterEntity {
    @Id
    private String id;
    private String topic;
    private String tldr;
    private float[] centroid;
    private List<String> keypoints;
    private List<String> articleIds;

    @Indexed
    private String userId;

    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    protected ClusterEntity() {
    }

    public ClusterEntity(String id, String topic, String tldr, float[] centroid,
                         List<String> keypoints, List<String> articleIds,
                         String userId, Instant createdAt) {
        this.id = id;
        this.topic = topic;
        this.tldr = tldr;
        this.centroid = centroid;
        this.keypoints = keypoints;
        this.articleIds = articleIds;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return UUID.fromString(id); }
    public String getTopic() { return topic; }
    public String getTldr() { return tldr; }
    public float[] getCentroid() { return centroid; }
    public List<String> getKeypoints() { return keypoints; }
    public List<String> getArticleIds() { return articleIds; }
    public String getUserId() { return userId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
