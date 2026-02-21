package com.krstf.newsfeed.adapter.outbound.repository.reads;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document(collection = "full_articles")
public class FullArticleEntity {
    @Id
    private String id;
    private String title;
    private String content;
    private String url;
    private Date publishedAt;
    private String analysis = null;

    @Indexed
    private String sourceId;
    private String sourceName;

    @Indexed
    private List<String> categories;

    @Indexed
    private AnalysisRequestStatus analysisRequestStatus;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Version
    private Long version;

    public FullArticleEntity() {
    }

    public FullArticleEntity(
            String id,
            String title,
            String content,
            String url,
            Date publishedAt,
            String analysis,
            String sourceId,
            String sourceName,
            List<String> categories,
            AnalysisRequestStatus analysisRequestStatus,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.publishedAt = publishedAt;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.categories = categories;
        this.analysisRequestStatus = analysisRequestStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public AnalysisRequestStatus getAnalysisRequestStatus() {
        return analysisRequestStatus;
    }

    public void setAnalysisRequestStatus(AnalysisRequestStatus analysisRequestStatus) {
        this.analysisRequestStatus = analysisRequestStatus;
    }

    public String getAnalysis() {
        return analysis;
    }
    

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}

