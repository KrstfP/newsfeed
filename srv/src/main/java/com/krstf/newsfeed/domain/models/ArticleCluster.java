package com.krstf.newsfeed.domain.models;

import java.time.Instant;
import java.util.ArrayList;
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

    public static ArticleCluster create(RssItem article, String userId) {
        return new ArticleCluster(
                UUID.randomUUID(),
                null,
                null,
                article.getSemanticVector() != null ? article.getSemanticVector().clone() : null,
                new ArrayList<>(),
                new ArrayList<>(List.of(article.getId())),
                Instant.now(),
                userId
        );
    }

    public boolean matches(float[] vector, float threshold) {
        if (this.centroid == null || vector == null) {
            return false;
        }
        return cosineSimilarity(this.centroid, vector) >= threshold;
    }

    public void addArticle(RssItem article) {
        articleIds.add(article.getId());
        if (article.getSemanticVector() != null && this.centroid != null) {
            this.centroid = recomputeCentroid(this.centroid, articleIds.size() - 1, article.getSemanticVector());
        }
    }

    private static float cosineSimilarity(float[] a, float[] b) {
        float dot = 0f, normA = 0f, normB = 0f;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0f || normB == 0f) return 0f;
        return dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB));
    }

    // Recalcule la moyenne incrémentalement : newCentroid = (oldCentroid * n + newVector) / (n + 1)
    private static float[] recomputeCentroid(float[] oldCentroid, int oldSize, float[] newVector) {
        float[] result = new float[oldCentroid.length];
        for (int i = 0; i < oldCentroid.length; i++) {
            result[i] = (oldCentroid[i] * oldSize + newVector[i]) / (oldSize + 1);
        }
        return result;
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
