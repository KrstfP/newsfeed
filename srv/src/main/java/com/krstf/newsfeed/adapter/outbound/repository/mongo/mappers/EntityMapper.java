package com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EntityMapper {

    public RssItem toDomain(ArticleEntity entity) {
        RssItem rssItem = new RssItem(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId(),
                entity.getSourceName(),
                entity.getUserId()
        );
        rssItem.setCategories(entity.getCategories());
        rssItem.setAnalysisStatus(AnalysisRequestStatus.valueOf(entity.getAnalysisStatus()));
        rssItem.setAnalysis(entity.getAnalysis());
        return rssItem;
    }

    public ArticleEntity toEntity(RssItem rssItem) {
        ArticleEntity entity = new ArticleEntity(
                rssItem.getId().toString(),
                rssItem.getTitle(),
                rssItem.getContent(),
                rssItem.getUrl(),
                rssItem.getPublishedAt(),
                rssItem.getSourceId(),
                rssItem.getSourceName(),
                rssItem.getCategories()
        );
        entity.setUserId(rssItem.getUserId());
        entity.setAnalysisStatus(rssItem.getAnalysisStatus().name());
        entity.setAnalysis(rssItem.getAnalysis());
        return entity;
    }

    public FullArticleDto toFullArticleDto(ArticleEntity entity) {
        return new FullArticleDto(
                entity.getId().toString(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId().toString(),
                entity.getSourceName(),
                entity.getCategories(),
                entity.getAnalysisStatus(),
                entity.getAnalysis()
        );
    }

    public RssFeedSource toDomain(SourceEntity entity) {
        return new RssFeedSource(
                entity.getId(),
                entity.getRssFeedUrl(),
                entity.getName(),
                entity.getDescription(),
                entity.getUserId()
        );
    }

    public SourceEntity toEntity(RssFeedSource rssFeedSource) {
        return new SourceEntity(
                rssFeedSource.getId().toString(),
                rssFeedSource.getRssFeedUrl(),
                rssFeedSource.getName(),
                rssFeedSource.getDescription(),
                rssFeedSource.getUserId()
        );
    }
}
