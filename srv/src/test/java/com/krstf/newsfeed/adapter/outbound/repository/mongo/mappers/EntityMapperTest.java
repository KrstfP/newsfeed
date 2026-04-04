package com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ClusterEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.*;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityMapperTest {

    private final EntityMapper mapper = new EntityMapper();

    // --- helpers ---

    private ArticleEntity anyArticleEntity() {
        ArticleEntity entity = new ArticleEntity(
                UUID.randomUUID().toString(),
                "titre",
                "contenu",
                "http://example.com/article",
                new Date(1_000_000L),
                UUID.randomUUID(),
                "Source Name",
                List.of("politique", "défense")
        );
        entity.setUserId("user1");
        entity.setAnalysisStatus("COMPLETED");
        entity.setAnalysis("résumé");
        return entity;
    }

    private RssItem anyRssItem() {
        RssItem item = new RssItem(
                UUID.randomUUID(),
                "titre",
                "contenu",
                "http://example.com/article",
                new Date(1_000_000L),
                UUID.randomUUID(),
                "Source Name",
                "user1"
        );
        item.setCategories(List.of("politique"));
        item.setAnalysisStatus(AnalysisRequestStatus.COMPLETED);
        item.setAnalysis("résumé");
        return item;
    }

    private SourceEntity sourceEntity(String status) {
        return new SourceEntity(
                UUID.randomUUID().toString(),
                URI.create("https://example.com/rss"),
                "Le Monde",
                "desc",
                "user1",
                status
        );
    }

    // --- toDomain(ArticleEntity) ---

    @Test
    void toDomain_article_mapsAllScalarFields() {
        ArticleEntity entity = anyArticleEntity();

        RssItem item = mapper.toDomain(entity);

        assertEquals(entity.getId(), item.getId());
        assertEquals(entity.getTitle(), item.getTitle());
        assertEquals(entity.getContent(), item.getContent());
        assertEquals(entity.getUrl(), item.getUrl());
        assertEquals(entity.getPublishedAt(), item.getPublishedAt());
        assertEquals(entity.getSourceId(), item.getSourceId());
        assertEquals(entity.getSourceName(), item.getSourceName());
        assertEquals(entity.getUserId(), item.getUserId());
    }

    @Test
    void toDomain_article_mapsAnalysisFields() {
        ArticleEntity entity = anyArticleEntity();

        RssItem item = mapper.toDomain(entity);

        assertEquals(AnalysisRequestStatus.COMPLETED, item.getAnalysisStatus());
        assertEquals("résumé", item.getAnalysis());
    }

    @Test
    void toDomain_article_mapsCategories() {
        ArticleEntity entity = anyArticleEntity();

        RssItem item = mapper.toDomain(entity);

        assertEquals(List.of("politique", "défense"), item.getCategories());
    }

    @Test
    void toDomain_article_mapsSemanticVector() {
        ArticleEntity entity = anyArticleEntity();
        float[] vector = {0.1f, 0.2f, 0.3f};
        entity.setSemanticVector(vector);

        RssItem item = mapper.toDomain(entity);

        assertArrayEquals(vector, item.getSemanticVector());
    }

    @Test
    void toDomain_article_nullSemanticVector_remainsNull() {
        ArticleEntity entity = anyArticleEntity();

        RssItem item = mapper.toDomain(entity);

        assertNull(item.getSemanticVector());
    }

    // --- toEntity(RssItem) ---

    @Test
    void toEntity_article_mapsAllScalarFields() {
        RssItem item = anyRssItem();

        ArticleEntity entity = mapper.toEntity(item);

        assertEquals(item.getId(), entity.getId());
        assertEquals(item.getTitle(), entity.getTitle());
        assertEquals(item.getContent(), entity.getContent());
        assertEquals(item.getUrl(), entity.getUrl());
        assertEquals(item.getPublishedAt(), entity.getPublishedAt());
        assertEquals(item.getSourceId(), entity.getSourceId());
        assertEquals(item.getSourceName(), entity.getSourceName());
        assertEquals(item.getUserId(), entity.getUserId());
    }

    @Test
    void toEntity_article_mapsAnalysisFields() {
        RssItem item = anyRssItem();

        ArticleEntity entity = mapper.toEntity(item);

        assertEquals("COMPLETED", entity.getAnalysisStatus());
        assertEquals("résumé", entity.getAnalysis());
    }

    @Test
    void toEntity_article_mapsSemanticVector() {
        RssItem item = anyRssItem();
        float[] vector = {0.4f, 0.5f};
        item.setSemanticVector(vector);

        ArticleEntity entity = mapper.toEntity(item);

        assertArrayEquals(vector, entity.getSemanticVector());
    }

    @Test
    void toEntity_article_nullSemanticVector_remainsNull() {
        RssItem item = anyRssItem();

        ArticleEntity entity = mapper.toEntity(item);

        assertNull(entity.getSemanticVector());
    }

    // --- toFullArticleDto ---

    @Test
    void toFullArticleDto_mapsAllFields() {
        ArticleEntity entity = anyArticleEntity();

        FullArticleDto dto = mapper.toFullArticleDto(entity);

        assertEquals(entity.getId().toString(), dto.id());
        assertEquals(entity.getTitle(), dto.title());
        assertEquals(entity.getContent(), dto.content());
        assertEquals(entity.getUrl(), dto.url());
        assertEquals(entity.getPublishedAt(), dto.publishedAt());
        assertEquals(entity.getSourceId().toString(), dto.sourceId());
        assertEquals(entity.getSourceName(), dto.source());
        assertEquals(entity.getCategories(), dto.categories());
        assertEquals(entity.getAnalysisStatus(), dto.analysisRequestStatus());
        assertEquals(entity.getAnalysis(), dto.analysis());
    }

    // --- toDomain(SourceEntity) ---

    @Test
    void toDomain_source_mapsAllFields() {
        SourceEntity entity = sourceEntity("ACTIVE");

        RssFeedSource source = mapper.toDomain(entity);

        assertEquals(entity.getId(), source.getId());
        assertEquals(entity.getRssFeedUrl(), source.getRssFeedUrl());
        assertEquals(entity.getName(), source.getName());
        assertEquals(entity.getDescription(), source.getDescription());
        assertEquals(entity.getUserId(), source.getUserId());
        assertEquals(RssFeedSourceStatus.ACTIVE, source.getStatus());
    }

    @Test
    void toDomain_source_nullStatus_defaultsToActive() {
        SourceEntity entity = sourceEntity(null);

        RssFeedSource source = mapper.toDomain(entity);

        assertEquals(RssFeedSourceStatus.ACTIVE, source.getStatus());
    }

    @Test
    void toDomain_source_deletedStatus_mapsCorrectly() {
        SourceEntity entity = sourceEntity("DELETED");

        RssFeedSource source = mapper.toDomain(entity);

        assertEquals(RssFeedSourceStatus.DELETED, source.getStatus());
    }

    // --- toEntity(RssFeedSource) ---

    @Test
    void toEntity_source_mapsAllFields() {
        RssFeedSource source = new RssFeedSource(
                UUID.randomUUID(),
                URI.create("https://example.com/rss"),
                "Le Monde",
                "desc",
                "user1",
                RssFeedSourceStatus.ACTIVE
        );

        SourceEntity entity = mapper.toEntity(source);

        assertEquals(source.getId().toString(), entity.getId().toString());
        assertEquals(source.getRssFeedUrl(), entity.getRssFeedUrl());
        assertEquals(source.getName(), entity.getName());
        assertEquals(source.getDescription(), entity.getDescription());
        assertEquals(source.getUserId(), entity.getUserId());
        assertEquals("ACTIVE", entity.getStatus());
    }

    // --- toDomain(ClusterEntity) ---

    private ClusterEntity anyClusterEntity() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        return new ClusterEntity(
                UUID.randomUUID().toString(),
                "conflit Proche-Orient",
                "Résumé en une phrase",
                new float[]{0.1f, 0.2f},
                List.of("frappe aérienne", "cessez-le-feu"),
                List.of(id1.toString(), id2.toString()),
                "user1",
                Instant.ofEpochSecond(1_000_000)
        );
    }

    @Test
    void toDomain_cluster_mapsAllScalarFields() {
        ClusterEntity entity = anyClusterEntity();

        ArticleCluster cluster = mapper.toDomain(entity);

        assertEquals(entity.getId(), cluster.getId());
        assertEquals(entity.getTopic(), cluster.getTopic());
        assertEquals(entity.getTldr(), cluster.getTldr());
        assertArrayEquals(entity.getCentroid(), cluster.getCentroid());
        assertEquals(entity.getKeypoints(), cluster.getKeypoints());
        assertEquals(entity.getUserId(), cluster.getUserId());
        assertEquals(entity.getCreatedAt(), cluster.getCreatedAt());
    }

    @Test
    void toDomain_cluster_convertsArticleIdsToUUID() {
        ClusterEntity entity = anyClusterEntity();

        ArticleCluster cluster = mapper.toDomain(entity);

        assertEquals(2, cluster.getArticleIds().size());
        cluster.getArticleIds().forEach(id -> assertInstanceOf(UUID.class, id));
        assertEquals(
                entity.getArticleIds().stream().map(UUID::fromString).toList(),
                cluster.getArticleIds()
        );
    }

    // --- toEntity(ArticleCluster) ---

    private ArticleCluster anyCluster() {
        return new ArticleCluster(
                UUID.randomUUID(),
                "conflit Proche-Orient",
                "Résumé en une phrase",
                new float[]{0.1f, 0.2f},
                List.of("frappe aérienne", "cessez-le-feu"),
                new java.util.ArrayList<>(List.of(UUID.randomUUID(), UUID.randomUUID())),
                Instant.ofEpochSecond(1_000_000),
                "user1"
        );
    }

    @Test
    void toEntity_cluster_mapsAllScalarFields() {
        ArticleCluster cluster = anyCluster();

        ClusterEntity entity = mapper.toEntity(cluster);

        assertEquals(cluster.getId().toString(), entity.getId().toString());
        assertEquals(cluster.getTopic(), entity.getTopic());
        assertEquals(cluster.getTldr(), entity.getTldr());
        assertArrayEquals(cluster.getCentroid(), entity.getCentroid());
        assertEquals(cluster.getKeypoints(), entity.getKeypoints());
        assertEquals(cluster.getUserId(), entity.getUserId());
        assertEquals(cluster.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void toEntity_cluster_convertsArticleIdsToString() {
        ArticleCluster cluster = anyCluster();

        ClusterEntity entity = mapper.toEntity(cluster);

        assertEquals(2, entity.getArticleIds().size());
        assertEquals(
                cluster.getArticleIds().stream().map(UUID::toString).toList(),
                entity.getArticleIds()
        );
    }
}
