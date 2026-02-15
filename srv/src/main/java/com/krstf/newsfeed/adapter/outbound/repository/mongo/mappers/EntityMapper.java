package com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestStatus;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.AnalysisStatus;
import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.domain.models.Source;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EntityMapper {

    public AnalysisRequest toDomain(RequestEntity entity) {
        AnalysisStatus analysisStatus = AnalysisStatus.NOT_REQUESTED;
        switch (entity.getStatus()) {
            case RequestStatus.PENDING -> analysisStatus = AnalysisStatus.PENDING;
            case RequestStatus.IN_PROGRESS -> analysisStatus = AnalysisStatus.IN_PROGRESS;
            case RequestStatus.COMPLETED -> analysisStatus = AnalysisStatus.COMPLETED;
            case RequestStatus.FAILED -> analysisStatus = AnalysisStatus.FAILED;
        }

        return new AnalysisRequest(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getArticleId()),
                analysisStatus,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public RequestEntity toEntity(AnalysisRequest analysisRequest) {
        RequestStatus requestStatus = RequestStatus.PENDING;
        switch (analysisRequest.getStatus()) {
            case PENDING -> requestStatus = RequestStatus.PENDING;
            case IN_PROGRESS -> requestStatus = RequestStatus.IN_PROGRESS;
            case COMPLETED -> requestStatus = RequestStatus.COMPLETED;
            case FAILED -> requestStatus = RequestStatus.FAILED;
        }

        return new RequestEntity(
                analysisRequest.getArticleId().toString(),
                analysisRequest.getCreatedAt(),
                analysisRequest.getUpdatedAt(),
                requestStatus,
                analysisRequest.getVersion()
        );
    }

    public Source toDomain(SourceEntity entity) {
        return new Source(
                entity.getRssFeedUrl(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public SourceEntity toEntity(Source source) {
        return new SourceEntity(
                source.getId().toString(),
                source.getRssFeedUrl(),
                source.getName(),
                source.getDescription()
        );
    }

    public Article toDomain(ArticleEntity entity) {
        Article article = new Article(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId(),
                entity.getSourceName()
        );
        article.setCategories(entity.getCategories());
        return article;
    }

    public ArticleEntity toEntity(Article article) {
        return new ArticleEntity(
                article.getId().toString(),
                article.getTitle(),
                article.getContent(),
                article.getUrl(),
                article.getPublishedAt(),
                article.getSourceId(),
                article.getSourceName(),
                article.getCategories()
        );
    }

}
