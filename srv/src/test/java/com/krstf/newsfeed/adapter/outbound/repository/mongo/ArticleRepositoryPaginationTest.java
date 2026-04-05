package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.port.outbound.repository.ArticleFilters;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import com.krstf.newsfeed.port.outbound.repository.PagedArticlesResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleRepositoryPaginationTest {

    @Mock SpringMongoArticleRepository springMongoArticleRepository;
    @Mock EntityMapper entityMapper;
    @Mock MongoTemplate mongoTemplate;

    @InjectMocks ArticleRepository repository;

    private ArticleEntity entityWithPublishedAt(Date publishedAt) {
        ArticleEntity e = new ArticleEntity(
                UUID.randomUUID().toString(), "titre", "contenu",
                "http://example.com", publishedAt, UUID.randomUUID(), "source", List.of()
        );
        e.setUserId("user1");
        e.setAnalysisStatus("NOT_REQUESTED");
        return e;
    }

    private FullArticleDto dtoWithPublishedAt(Date publishedAt) {
        return new FullArticleDto(UUID.randomUUID().toString(), "titre", "contenu",
                "http://example.com", publishedAt, UUID.randomUUID().toString(),
                "source", List.of(), "NOT_REQUESTED", null);
    }

    private List<ArticleEntity> entitiesOfSize(int count) {
        long base = 1_000_000_000L;
        List<ArticleEntity> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(entityWithPublishedAt(new Date(base - i * 1000L)));
        }
        return list;
    }

    // --- nextPageToken ---

    @Test
    void getFullArticles_pageFull_nextPageTokenIsNotNull() {
        int limit = 3;
        List<ArticleEntity> entities = entitiesOfSize(limit);
        entities.forEach(e -> when(entityMapper.toFullArticleDto(e))
                .thenReturn(dtoWithPublishedAt(e.getPublishedAt())));
        when(mongoTemplate.find(any(Query.class), eq(ArticleEntity.class))).thenReturn(entities);

        PagedArticlesResponse response = repository.getFullArticles("user1",
                new ArticleFilters(null, null, limit, null));

        assertNotNull(response.nextPageToken());
    }

    @Test
    void getFullArticles_pagePartial_nextPageTokenIsNull() {
        int limit = 10;
        List<ArticleEntity> entities = entitiesOfSize(3); // moins que limit
        entities.forEach(e -> when(entityMapper.toFullArticleDto(e))
                .thenReturn(dtoWithPublishedAt(e.getPublishedAt())));
        when(mongoTemplate.find(any(Query.class), eq(ArticleEntity.class))).thenReturn(entities);

        PagedArticlesResponse response = repository.getFullArticles("user1",
                new ArticleFilters(null, null, limit, null));

        assertNull(response.nextPageToken());
    }

    @Test
    void getFullArticles_emptyResult_nextPageTokenIsNull() {
        when(mongoTemplate.find(any(Query.class), eq(ArticleEntity.class))).thenReturn(List.of());

        PagedArticlesResponse response = repository.getFullArticles("user1",
                new ArticleFilters(null, null, 10, null));

        assertNull(response.nextPageToken());
        assertTrue(response.articles().isEmpty());
    }

    // --- limit appliqué à la query ---

    @Test
    void getFullArticles_limitAppliedToQuery() {
        when(mongoTemplate.find(any(Query.class), eq(ArticleEntity.class))).thenReturn(List.of());
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);

        repository.getFullArticles("user1", new ArticleFilters(null, null, 42, null));

        verify(mongoTemplate).find(queryCaptor.capture(), eq(ArticleEntity.class));
        assertEquals(42, queryCaptor.getValue().getLimit());
    }

    // --- round-trip du token ---

    @Test
    void getFullArticles_tokenRoundTrip_cursorMatchesLastPublishedAt() {
        Date publishedAt = new Date(1_700_000_000_000L);
        ArticleEntity entity = entityWithPublishedAt(publishedAt);
        FullArticleDto dto = dtoWithPublishedAt(publishedAt);
        when(entityMapper.toFullArticleDto(entity)).thenReturn(dto);
        when(mongoTemplate.find(any(Query.class), eq(ArticleEntity.class)))
                .thenReturn(List.of(entity))   // première page : 1 article == limit → génère un token
                .thenReturn(List.of());         // deuxième page : vide

        // Première page — récupère le token
        PagedArticlesResponse first = repository.getFullArticles("user1",
                new ArticleFilters(null, null, 1, null));
        String token = first.nextPageToken();
        assertNotNull(token);

        // Deuxième page — passe le token
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        repository.getFullArticles("user1", new ArticleFilters(null, null, 1, token));

        verify(mongoTemplate, org.mockito.Mockito.times(2))
                .find(captor.capture(), eq(ArticleEntity.class));

        // La query de la deuxième page doit contenir un critère sur publishedAt
        Query secondQuery = captor.getAllValues().get(1);
        String queryString = secondQuery.getQueryObject().toJson();
        assertTrue(queryString.contains("publishedAt"),
                "La query doit filtrer sur publishedAt quand un pageToken est fourni");
    }
}
