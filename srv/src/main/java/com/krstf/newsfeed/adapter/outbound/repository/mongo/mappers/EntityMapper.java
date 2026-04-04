package com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ClusterEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssFeedSourceStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import org.springframework.stereotype.Service;

import java.util.List;
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
        rssItem.setSemanticVector(entity.getSemanticVector());
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
        entity.setSemanticVector(rssItem.getSemanticVector());
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
        // Les documents sans status (avant migration) sont traités comme ACTIVE
        RssFeedSourceStatus status = entity.getStatus() != null
                ? RssFeedSourceStatus.valueOf(entity.getStatus())
                : RssFeedSourceStatus.ACTIVE;
        return new RssFeedSource(
                entity.getId(),
                entity.getRssFeedUrl(),
                entity.getName(),
                entity.getDescription(),
                entity.getUserId(),
                status
        );
    }

    public SourceEntity toEntity(RssFeedSource rssFeedSource) {
        return new SourceEntity(
                rssFeedSource.getId().toString(),
                rssFeedSource.getRssFeedUrl(),
                rssFeedSource.getName(),
                rssFeedSource.getDescription(),
                rssFeedSource.getUserId(),
                rssFeedSource.getStatus().name()
        );
    }

    public ArticleCluster toDomain(ClusterEntity entity) {
        List<UUID> articleIds = entity.getArticleIds().stream()
                .map(UUID::fromString)
                .toList();
        return new ArticleCluster(
                entity.getId(),
                entity.getTopic(),
                entity.getTldr(),
                entity.getCentroid(),
                entity.getKeypoints(),
                articleIds,
                entity.getCreatedAt(),
                entity.getUserId()
        );
    }

    public ClusterEntity toEntity(ArticleCluster cluster) {
        List<String> articleIds = cluster.getArticleIds().stream()
                .map(UUID::toString)
                .toList();
        return new ClusterEntity(
                cluster.getId().toString(),
                cluster.getTopic(),
                cluster.getTldr(),
                cluster.getCentroid(),
                cluster.getKeypoints(),
                articleIds,
                cluster.getUserId(),
                cluster.getCreatedAt()
        );
    }
}
