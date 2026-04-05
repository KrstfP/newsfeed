package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummarizer;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.NotificationChangeType;
import com.krstf.newsfeed.port.outbound.notification.NotificationObjectType;
import com.krstf.newsfeed.port.outbound.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterArticlesUseCaseServiceTest {

    @Mock GetArticle getArticle;
    @Mock GetCluster getCluster;
    @Mock SaveCluster saveCluster;
    @Mock DeleteCluster deleteCluster;
    @Mock GetFullArticle getFullArticle;
    @Mock ClusterSummarizer clusterSummarizer;

    @InjectMocks ClusterArticlesUseCaseService service;

    private static final float[] ANY_VECTOR = {1f, 0f, 0f};
    private static final ClusterSummary ANY_SUMMARY = new ClusterSummary("topic", "tldr", List.of("k1"));

    @BeforeEach
    void setUp() {
        lenient().when(getCluster.getByUserId(anyString(), any(LocalDate.class))).thenReturn(List.of());
        lenient().when(getFullArticle.getFullArticles(anyString(), any())).thenReturn(new PagedArticlesResponse(List.of(), null));
        lenient().when(clusterSummarizer.summarize(any())).thenReturn(ANY_SUMMARY);
        lenient().when(saveCluster.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private RssItem articleWithVector(float[] vector) {
        RssItem article = new RssItem(UUID.randomUUID(), "titre", "contenu", "https://example.com/1",
                new Date(), UUID.randomUUID(), "Example", "user1");
        article.setSemanticVector(vector);
        return article;
    }

    private RssItem articleWithoutVector() {
        return new RssItem(UUID.randomUUID(), "titre", "contenu", "https://example.com/1",
                new Date(), UUID.randomUUID(), "Example", "user1");
    }

    private ArticleCluster clusterMatchingVector(float[] centroid) {
        return new ArticleCluster(UUID.randomUUID(), "topic", "tldr", centroid,
                new ArrayList<>(), new ArrayList<>(), Instant.now(), "user1");
    }

    private ArticleNotification createdNotification(RssItem article) {
        return new ArticleNotification(
                article.getId(), NotificationObjectType.ARTICLE,
                NotificationChangeType.ARTICLE_CREATED, article.getUserId(), null, null);
    }

    // --- onStatusChanged: filtre les événements non-ARTICLE_CREATED ---

    @Test
    void onStatusChanged_analysisStatusChanged_ignored() {
        ArticleNotification notification = new ArticleNotification(
                UUID.randomUUID(), NotificationObjectType.ARTICLE,
                NotificationChangeType.ANALYSIS_STATUS_CHANGED, "user1", "PENDING", "IN_PROGRESS");

        service.onStatusChanged(notification);

        verifyNoInteractions(getArticle, getCluster, saveCluster);
    }

    // --- onStatusChanged: ARTICLE_CREATED déclenche le clustering ---

    @Test
    void onStatusChanged_articleCreated_noVector_clusteringSkipped() {
        RssItem article = articleWithoutVector();
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.onStatusChanged(createdNotification(article));

        verifyNoInteractions(getCluster, saveCluster);
    }

    @Test
    void onStatusChanged_articleCreated_withVector_getClusterCalledWithWeekStart() {
        RssItem article = articleWithVector(ANY_VECTOR);
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.onStatusChanged(createdNotification(article));

        LocalDate expectedWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        verify(getCluster).getByUserId("user1", expectedWeekStart);
    }

    @Test
    void onStatusChanged_articleCreated_matchingCluster_articleAddedToCluster() {
        RssItem article = articleWithVector(ANY_VECTOR);
        ArticleCluster existingCluster = clusterMatchingVector(ANY_VECTOR.clone());
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));
        when(getCluster.getByUserId(anyString(), any())).thenReturn(List.of(existingCluster));

        service.onStatusChanged(createdNotification(article));

        ArgumentCaptor<ArticleCluster> captor = ArgumentCaptor.forClass(ArticleCluster.class);
        verify(saveCluster).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(existingCluster.getId());
        assertThat(captor.getValue().getArticleIds()).contains(article.getId());
    }

    @Test
    void onStatusChanged_articleCreated_noMatchingCluster_newClusterCreatedAndSaved() {
        RssItem article = articleWithVector(ANY_VECTOR);
        ArticleCluster nonMatchingCluster = clusterMatchingVector(new float[]{0f, 1f, 0f});
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));
        when(getCluster.getByUserId(anyString(), any())).thenReturn(List.of(nonMatchingCluster));

        service.onStatusChanged(createdNotification(article));

        ArgumentCaptor<ArticleCluster> captor = ArgumentCaptor.forClass(ArticleCluster.class);
        verify(saveCluster).save(captor.capture());
        assertThat(captor.getValue().getId()).isNotEqualTo(nonMatchingCluster.getId());
        assertThat(captor.getValue().getArticleIds()).containsExactly(article.getId());
    }

    @Test
    void onStatusChanged_articleCreated_getClusterThrows_doesNotPropagate() {
        RssItem article = articleWithVector(ANY_VECTOR);
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));
        when(getCluster.getByUserId(anyString(), any())).thenThrow(new RuntimeException("DB error"));

        // doit swallower l'exception
        service.onStatusChanged(createdNotification(article));

        verify(saveCluster, never()).save(any());
    }

    @Test
    void onStatusChanged_articleCreated_noSummarizer_clusterSaved() {
        RssItem article = articleWithVector(ANY_VECTOR);
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.onStatusChanged(createdNotification(article));

        verify(saveCluster).save(any());
    }

    // --- rebuildClusters ---

    @Test
    void rebuildClusters_deletesExistingClusters() {
        when(getArticle.getAllByUserId("user1")).thenReturn(List.of());

        service.rebuildClusters("user1");

        verify(deleteCluster).deleteAllByUserId("user1");
    }

    @Test
    void rebuildClusters_processesAllUserArticles() {
        RssItem a1 = articleWithVector(ANY_VECTOR);
        RssItem a2 = articleWithVector(new float[]{0f, 1f, 0f});
        when(getArticle.getAllByUserId("user1")).thenReturn(List.of(a1, a2));

        service.rebuildClusters("user1");

        // 2 articles sans vecteur commun → 2 nouveaux clusters
        verify(saveCluster, times(2)).save(any());
    }

    @Test
    void rebuildClusters_articlesWithoutVector_skipped() {
        RssItem withVector = articleWithVector(ANY_VECTOR);
        RssItem withoutVector = articleWithoutVector();
        when(getArticle.getAllByUserId("user1")).thenReturn(List.of(withVector, withoutVector));

        service.rebuildClusters("user1");

        verify(saveCluster, times(1)).save(any());
    }

    @Test
    void rebuildClusters_emptyArticleList_noClustersCreated() {
        when(getArticle.getAllByUserId("user1")).thenReturn(List.of());

        service.rebuildClusters("user1");

        verify(saveCluster, never()).save(any());
    }
}
