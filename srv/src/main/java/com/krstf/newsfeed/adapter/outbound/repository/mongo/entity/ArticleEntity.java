package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "articles")
public class ArticleEntity {
    @Id
    private String id;

    private String title;
    private String content;
    private String url;
    private Date publishedAt;

    @Indexed
    private String sourceId;

    private String sourceName;

    protected ArticleEntity() {
    }

    @Indexed
    private List<String> categories;

    @Indexed
    private String userId;

    @Indexed
    private String analysisStatus = "NOT_REQUESTED";
    private String analysis = null;
    private float[] semanticVector = null;

    @LastModifiedDate
    private Instant updatedAt;

    public ArticleEntity(
            String id,
            String title,
            String content,
            String url,
            Date publishedAt,
            UUID sourceId,
            String sourceName,
            List<String> categories
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
        this.sourceId = sourceId.toString();
        this.categories = categories;
    }

    // getters uniquement
    public UUID getId() {
        return UUID.fromString(id);
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

    public UUID getSourceId() {
        return UUID.fromString(this.sourceId);
    }

    public String getSourceName() {
        return sourceName;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float[] getSemanticVector() {
        return semanticVector;
    }

    public void setSemanticVector(float[] semanticVector) {
        this.semanticVector = semanticVector;
    }

}
