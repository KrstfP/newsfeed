package com.krstf.newsfeed.domain.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ArticleCluster {
    private final UUID id;
    private String topic;
    private String tldr;
    private float[] centroid;
    private List<String> keypoints;
    private List<UUID> articleIds;
    private final Instant createdAt;
    private final String userId;

    public ArticleCluster(UUID id, String topic, String tldr, float[] centroid,
                          List<String> keypoints, List<UUID> articleIds,
                          Instant createdAt, String userId) {
        this.id = id;
        this.topic = topic;
        this.tldr = tldr;
        this.centroid = centroid;
        this.keypoints = keypoints;
        this.articleIds = articleIds;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getTldr() { return tldr; }
    public float[] getCentroid() { return centroid; }
    public List<String> getKeypoints() { return keypoints; }
    public List<UUID> getArticleIds() { return articleIds; }
    public Instant getCreatedAt() { return createdAt; }
    public String getUserId() { return userId; }

    public void setTopic(String topic) { this.topic = topic; }
    public void setTldr(String tldr) { this.tldr = tldr; }
    public void setCentroid(float[] centroid) { this.centroid = centroid; }
    public void setKeypoints(List<String> keypoints) { this.keypoints = keypoints; }
    public void setArticleIds(List<UUID> articleIds) { this.articleIds = articleIds; }
}
