package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummarizer;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.ai.SemanticVectorizer;
import com.krstf.newsfeed.port.outbound.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadArticlesUseCaseServiceTest {

    @Mock
    GetSource getSource;
    @Mock
    ArticleLoader articleLoader;
    @Mock
    SaveArticle saveArticle;
    @Mock
    GetArticle getArticle;
    @Mock
    SemanticVectorizer semanticVectorizer;
    @Mock
    GetCluster getCluster;
    @Mock
    SaveCluster saveCluster;
    @Mock
    GetFullArticle getFullArticle;
    @Mock
    ClusterSummarizer clusterSummarizer;

    @InjectMocks
    LoadArticlesUseCaseService service;

    private static final float[] ANY_VECTOR = {1f, 0f, 0f};
    private static final ClusterSummary ANY_SUMMARY = new ClusterSummary("topic", "tldr", List.of("k1"));

    @BeforeEach
    void setUp() {
        // Stubs par défaut pour les interactions clustering — utilisés dans les tests
        // qui ne testent pas spécifiquement le clustering mais déclenchent quand même le chemin
        lenient().when(getCluster.getByUserId(anyString(), any(LocalDate.class))).thenReturn(List.of());
        lenient().when(getFullArticle.getFullArticles(anyString(), any())).thenReturn(List.of());
        lenient().when(clusterSummarizer.summarize(any())).thenReturn(ANY_SUMMARY);
        lenient().when(saveCluster.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private RssFeedSource anySource() {
        return new RssFeedSource(URI.create("https://example.com/rss"), "Example", "", "user1");
    }

    private RssItem anyArticle() {
        return new RssItem(UUID.randomUUID(), "titre", "contenu", "https://example.com/1",
                new Date(), UUID.randomUUID(), "Example", "user1");
    }

    private ArticleCluster clusterMatchingVector(float[] centroid) {
        return new ArticleCluster(UUID.randomUUID(), "topic", "tldr", centroid,
                new ArrayList<>(), new ArrayList<>(), Instant.now(), "user1");
    }

    // --- aucune source ---

    @Test
    void refreshArticles_noSources_doesNothing() {
        when(getSource.getAllSources()).thenReturn(List.of());

        service.refreshArticles();

        verifyNoInteractions(articleLoader, saveArticle, getArticle);
    }

    // --- happy path ---

    @Test
    void refreshArticles_newArticle_isSaved() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());

        service.refreshArticles();

        verify(saveArticle).saveArticle(article);
    }

    @Test
    void refreshArticles_articleAlreadyExists_isNotSavedAgain() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.refreshArticles();

        verify(saveArticle, never()).saveArticle(any());
    }

    // --- gestion d'erreur loader ---

    @Test
    void refreshArticles_loaderThrows_skipsSourceAndContinues() {
        RssFeedSource failingSource = anySource();
        RssFeedSource okSource = new RssFeedSource(URI.create("https://other.com/rss"), "Other", "", "user1");
        RssItem article = anyArticle();

        when(getSource.getAllSources()).thenReturn(List.of(failingSource, okSource));
        when(articleLoader.loadArticles(failingSource)).thenThrow(new RuntimeException("timeout"));
        when(articleLoader.loadArticles(okSource)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());

        service.refreshArticles();

        verify(saveArticle).saveArticle(article);
    }

    @Test
    void refreshArticles_loaderThrows_doesNotSaveFromFailingSource() {
        RssFeedSource source = anySource();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenThrow(new RuntimeException("connection refused"));

        service.refreshArticles();

        verifyNoInteractions(saveArticle, getArticle);
    }

    // --- vectorisation ---

    @Test
    void refreshArticles_newArticle_vectorIsAttachedBeforeSave() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);

        service.refreshArticles();

        assertThat(article.getSemanticVector()).isEqualTo(ANY_VECTOR);
        verify(saveArticle).saveArticle(article);
    }

    @Test
    void refreshArticles_vectorizerThrows_articleIsSavedWithoutVector() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenThrow(new RuntimeException("quota exceeded"));

        service.refreshArticles();

        assertThat(article.getSemanticVector()).isNull();
        verify(saveArticle).saveArticle(article);
    }

    @Test
    void refreshArticles_articleAlreadyExists_vectorizerIsNotCalled() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.refreshArticles();

        verifyNoInteractions(semanticVectorizer);
    }

    // --- clustering ---

    @Test
    void refreshArticles_newArticleWithVector_getClusterCalledWithWeekStart() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);

        service.refreshArticles();

        LocalDate expectedWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        verify(getCluster).getByUserId("user1", expectedWeekStart);
    }

    @Test
    void refreshArticles_matchingClusterFound_articleAddedToCluster() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        ArticleCluster existingCluster = clusterMatchingVector(ANY_VECTOR.clone());
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);
        when(getCluster.getByUserId(anyString(), any())).thenReturn(List.of(existingCluster));

        service.refreshArticles();

        ArgumentCaptor<ArticleCluster> captor = ArgumentCaptor.forClass(ArticleCluster.class);
        verify(saveCluster).save(captor.capture());
        assertThat(captor.getValue().getArticleIds()).contains(article.getId());
        assertThat(captor.getValue().getId()).isEqualTo(existingCluster.getId());
    }

    @Test
    void refreshArticles_noMatchingCluster_newClusterCreatedAndSaved() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        // Cluster existant avec vecteur orthogonal → pas de match
        ArticleCluster nonMatchingCluster = clusterMatchingVector(new float[]{0f, 1f, 0f});
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);
        when(getCluster.getByUserId(anyString(), any())).thenReturn(List.of(nonMatchingCluster));

        service.refreshArticles();

        ArgumentCaptor<ArticleCluster> captor = ArgumentCaptor.forClass(ArticleCluster.class);
        verify(saveCluster).save(captor.capture());
        assertThat(captor.getValue().getId()).isNotEqualTo(nonMatchingCluster.getId());
        assertThat(captor.getValue().getArticleIds()).containsExactly(article.getId());
    }

    @Test
    void refreshArticles_noVector_clusteringSkipped() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle(); // pas de vecteur
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());

        service.refreshArticles();

        verify(saveArticle).saveArticle(article);
        verifyNoInteractions(getCluster, saveCluster);
    }

    @Test
    void refreshArticles_articleAlreadyExists_clusteringSkipped() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.refreshArticles();

        verifyNoInteractions(getCluster, saveCluster);
    }

    @Test
    void refreshArticles_getClusterThrows_articleStillSaved() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);
        when(getCluster.getByUserId(anyString(), any())).thenThrow(new RuntimeException("DB error"));

        service.refreshArticles();

        verify(saveArticle).saveArticle(article);
        verify(saveCluster, never()).save(any());
    }

    @Test
    void refreshArticles_clusterSummarizerThrows_clusterStillSaved() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());
        when(semanticVectorizer.vectorizeText(anyString())).thenReturn(ANY_VECTOR);
        when(clusterSummarizer.summarize(any())).thenThrow(new RuntimeException("quota exceeded"));

        service.refreshArticles();

        verify(saveArticle).saveArticle(article);
        verify(saveCluster).save(any());
    }
}
