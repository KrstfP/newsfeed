package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ClusterEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.ArticleCluster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterRepositoryTest {

    @Mock SpringMongoClusterRepository springRepo;
    @Mock EntityMapper entityMapper;

    @InjectMocks ClusterRepository repository;

    private ArticleCluster anyCluster() {
        return new ArticleCluster(UUID.randomUUID(), "topic", "tldr", new float[]{0.1f},
                new ArrayList<>(), new ArrayList<>(), Instant.now(), "user1");
    }

    private ClusterEntity anyEntity() {
        return new ClusterEntity(UUID.randomUUID().toString(), "topic", "tldr",
                new float[]{0.1f}, List.of(), List.of(), "user1", Instant.now());
    }

    // --- save ---

    @Test
    void save_delegatesToSpringRepoAndMapsResult() {
        ArticleCluster cluster = anyCluster();
        ClusterEntity entity = anyEntity();
        ClusterEntity savedEntity = anyEntity();
        ArticleCluster savedCluster = anyCluster();

        when(entityMapper.toEntity(cluster)).thenReturn(entity);
        when(springRepo.save(entity)).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedCluster);

        ArticleCluster result = repository.save(cluster);

        assertSame(savedCluster, result);
    }

    // --- getById ---

    @Test
    void getById_found_returnsMappedCluster() {
        UUID id = UUID.randomUUID();
        ClusterEntity entity = anyEntity();
        ArticleCluster cluster = anyCluster();
        when(springRepo.findById(id.toString())).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(cluster);

        Optional<ArticleCluster> result = repository.getById(id);

        assertTrue(result.isPresent());
        assertSame(cluster, result.get());
    }

    @Test
    void getById_notFound_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(springRepo.findById(id.toString())).thenReturn(Optional.empty());

        assertTrue(repository.getById(id).isEmpty());
    }

    // --- getByUserId ---

    @Test
    void getByUserId_convertsLocalDateToInstantAndReturnsMappedList() {
        LocalDate since = LocalDate.of(2026, 4, 1);
        Instant expectedInstant = since.atStartOfDay(ZoneOffset.UTC).toInstant();
        ClusterEntity entity = anyEntity();
        ArticleCluster cluster = anyCluster();
        when(springRepo.findAllByUserIdAndCreatedAtGreaterThanEqual("user1", expectedInstant))
                .thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(cluster);

        List<ArticleCluster> result = repository.getByUserId("user1", since);

        assertEquals(1, result.size());
        assertSame(cluster, result.getFirst());
    }

    @Test
    void getByUserId_noClusters_returnsEmptyList() {
        LocalDate since = LocalDate.of(2026, 4, 1);
        when(springRepo.findAllByUserIdAndCreatedAtGreaterThanEqual(anyString(), any()))
                .thenReturn(List.of());

        assertTrue(repository.getByUserId("user1", since).isEmpty());
    }

    // --- getByArticleId ---

    @Test
    void getByArticleId_found_returnsMappedCluster() {
        UUID articleId = UUID.randomUUID();
        ClusterEntity entity = anyEntity();
        ArticleCluster cluster = anyCluster();
        when(springRepo.findFirstByArticleIdsContaining(articleId.toString()))
                .thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(cluster);

        Optional<ArticleCluster> result = repository.getByArticleId(articleId);

        assertTrue(result.isPresent());
        assertSame(cluster, result.get());
    }

    @Test
    void getByArticleId_notFound_returnsEmpty() {
        UUID articleId = UUID.randomUUID();
        when(springRepo.findFirstByArticleIdsContaining(articleId.toString()))
                .thenReturn(Optional.empty());

        assertTrue(repository.getByArticleId(articleId).isEmpty());
    }
}
