package com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestStatus;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EntityMapper {

    public AnalysisRequest toDomain(RequestEntity entity) {
        AnalysisRequestStatus analysisRequestStatus = AnalysisRequestStatus.NOT_REQUESTED;
        switch (entity.getStatus()) {
            case RequestStatus.PENDING -> analysisRequestStatus = AnalysisRequestStatus.PENDING;
            case RequestStatus.IN_PROGRESS -> analysisRequestStatus = AnalysisRequestStatus.IN_PROGRESS;
            case RequestStatus.COMPLETED -> analysisRequestStatus = AnalysisRequestStatus.COMPLETED;
            case RequestStatus.FAILED -> analysisRequestStatus = AnalysisRequestStatus.FAILED;
        }

        return new AnalysisRequest(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getArticleId()),
                analysisRequestStatus,
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

    public RssFeedSource toDomain(SourceEntity entity) {
        return new RssFeedSource(
                entity.getRssFeedUrl(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public SourceEntity toEntity(RssFeedSource rssFeedSource) {
        return new SourceEntity(
                rssFeedSource.getId().toString(),
                rssFeedSource.getRssFeedUrl(),
                rssFeedSource.getName(),
                rssFeedSource.getDescription()
        );
    }

    public RssItem toDomain(ArticleEntity entity) {
        RssItem rssItem = new RssItem(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId(),
                entity.getSourceName()
        );
        rssItem.setCategories(entity.getCategories());
        return rssItem;
    }

    public ArticleEntity toEntity(RssItem rssItem) {
        return new ArticleEntity(
                rssItem.getId().toString(),
                rssItem.getTitle(),
                rssItem.getContent(),
                rssItem.getUrl(),
                rssItem.getPublishedAt(),
                rssItem.getSourceId(),
                rssItem.getSourceName(),
                rssItem.getCategories()
        );
    }

}
