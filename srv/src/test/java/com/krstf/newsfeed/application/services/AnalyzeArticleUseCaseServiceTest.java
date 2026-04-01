package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.notification.Notifier;
import com.krstf.newsfeed.port.outbound.repository.AnalysisRequestQueue;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyzeArticleUseCaseServiceTest {

    @Mock GetArticle getArticle;
    @Mock ArticleAnalyzer articleAnalyzer;
    @Mock AnalysisRequestQueue analysisRequestQueue;
    @Mock Notifier notifier;

    @InjectMocks AnalyzeArticleUseCaseService service;

    private AnalysisRequest pendingRequest() {
        return new AnalysisRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                AnalysisRequestStatus.PENDING,
                Instant.now(),
                Instant.now(),
                0L
        );
    }

    private RssItem anyArticle(UUID id) {
        return new RssItem(id, "titre", "contenu", "http://example.com", new Date(), UUID.randomUUID(), "source");
    }

    // --- file vide ---

    @Test
    void execute_queueEmpty_doesNothing() {
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.empty());

        service.execute();

        verifyNoInteractions(getArticle, articleAnalyzer, notifier);
        verify(analysisRequestQueue, never()).save(any());
    }

    // --- article introuvable ---

    @Test
    void execute_articleNotFound_requestIsFailed() {
        AnalysisRequest request = pendingRequest();
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.empty());

        service.execute();

        assertEquals(AnalysisRequestStatus.FAILED, request.getStatus());
        verify(analysisRequestQueue, times(2)).save(request);
        verifyNoInteractions(articleAnalyzer);
        verifyNoInteractions(notifier);
    }

    // --- happy path ---

    @Test
    void execute_analysisSucceeds_requestIsCompleted() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenReturn("résumé");

        service.execute();

        assertEquals(AnalysisRequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void execute_analysisSucceeds_notificationsInOrder() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenReturn("résumé");

        service.execute();

        InOrder order = inOrder(analysisRequestQueue, notifier);
        order.verify(analysisRequestQueue).save(request);         // après start()
        order.verify(notifier).notifyAnalysisStarted(article);
        order.verify(analysisRequestQueue).save(request);         // après complete()
        order.verify(notifier).notifyAnalysisCompleted(article, "résumé");
        order.verifyNoMoreInteractions();
    }

    @Test
    void execute_analysisSucceeds_failureNotificationsNeverCalled() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenReturn("résumé");

        service.execute();

        verify(notifier, never()).notifyAnalysisFailed(any());
    }

    // --- analyzer lève une exception ---

    @Test
    void execute_analyzerThrows_requestIsFailed() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenThrow(new RuntimeException("timeout"));

        service.execute();

        assertEquals(AnalysisRequestStatus.FAILED, request.getStatus());
    }

    @Test
    void execute_analyzerThrows_notificationsInOrder() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenThrow(new RuntimeException("timeout"));

        service.execute();

        InOrder order = inOrder(analysisRequestQueue, notifier);
        order.verify(analysisRequestQueue).save(request);         // après start()
        order.verify(notifier).notifyAnalysisStarted(article);
        order.verify(analysisRequestQueue).save(request);         // après fail()
        order.verify(notifier).notifyAnalysisFailed(article);
        order.verifyNoMoreInteractions();
    }

    @Test
    void execute_analyzerThrows_completedNotificationNeverCalled() {
        AnalysisRequest request = pendingRequest();
        RssItem article = anyArticle(request.getArticleId());
        when(analysisRequestQueue.getNextPendingRequest()).thenReturn(Optional.of(request));
        when(getArticle.getArticleById(request.getArticleId())).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenThrow(new RuntimeException("timeout"));

        service.execute();

        verify(notifier, never()).notifyAnalysisCompleted(any(), any());
    }
}
