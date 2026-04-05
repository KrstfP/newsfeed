package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.ai.SemanticVectorizer;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.NotificationChangeType;
import com.krstf.newsfeed.port.outbound.notification.NotifyArticleStatusChange;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadArticlesUseCaseServiceTest {

    @Mock GetSource getSource;
    @Mock ArticleLoader articleLoader;
    @Mock SaveArticle saveArticle;
    @Mock GetArticle getArticle;
    @Mock SemanticVectorizer semanticVectorizer;
    @Mock NotifyArticleStatusChange notifyArticleStatusChange;

    @InjectMocks LoadArticlesUseCaseService service;

    private static final float[] ANY_VECTOR = {1f, 0f, 0f};

    private RssFeedSource anySource() {
        return new RssFeedSource(URI.create("https://example.com/rss"), "Example", "", "user1");
    }

    private RssItem anyArticle() {
        return new RssItem(UUID.randomUUID(), "titre", "contenu", "https://example.com/1",
                new Date(), UUID.randomUUID(), "Example", "user1");
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
    void refreshArticles_newArticle_articleCreatedNotificationEmitted() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.empty());

        service.refreshArticles();

        ArgumentCaptor<ArticleNotification> captor = ArgumentCaptor.forClass(ArticleNotification.class);
        verify(notifyArticleStatusChange).notify(captor.capture());
        assertThat(captor.getValue().changeType()).isEqualTo(NotificationChangeType.ARTICLE_CREATED);
        assertThat(captor.getValue().articleId()).isEqualTo(article.getId());
        assertThat(captor.getValue().userId()).isEqualTo("user1");
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

    @Test
    void refreshArticles_articleAlreadyExists_notificationNotEmitted() {
        RssFeedSource source = anySource();
        RssItem article = anyArticle();
        when(getSource.getAllSources()).thenReturn(List.of(source));
        when(articleLoader.loadArticles(source)).thenReturn(List.of(article));
        when(getArticle.getArticleById(article.getId())).thenReturn(Optional.of(article));

        service.refreshArticles();

        verifyNoInteractions(notifyArticleStatusChange);
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
}
