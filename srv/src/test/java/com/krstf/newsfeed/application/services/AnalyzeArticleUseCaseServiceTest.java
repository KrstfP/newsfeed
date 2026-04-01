package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyzeArticleUseCaseServiceTest {

    @Mock GetArticle getArticle;
    @Mock SaveArticle saveArticle;
    @Mock ArticleAnalyzer articleAnalyzer;

    @InjectMocks AnalyzeArticleUseCaseService service;

    private RssItem anyArticle() {
        return new RssItem(UUID.randomUUID(), "titre", "contenu", "http://example.com", new Date(), UUID.randomUUID(), "source");
    }

    // --- file vide ---

    @Test
    void execute_queueEmpty_doesNothing() {
        when(getArticle.getNextPendingArticle()).thenReturn(Optional.empty());

        service.execute();

        verifyNoInteractions(saveArticle, articleAnalyzer);
    }

    // --- happy path ---

    @Test
    void execute_analysisSucceeds_articleIsCompleted() {
        RssItem article = anyArticle();
        when(getArticle.getNextPendingArticle()).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenReturn("résumé");

        service.execute();

        assertEquals(AnalysisRequestStatus.COMPLETED, article.getAnalysisStatus());
        assertEquals("résumé", article.getAnalysis());
    }

    @Test
    void execute_analysisSucceeds_savesInOrder() {
        RssItem article = anyArticle();
        when(getArticle.getNextPendingArticle()).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenReturn("résumé");

        service.execute();

        InOrder order = inOrder(saveArticle, articleAnalyzer);
        order.verify(saveArticle).saveArticle(article);      // après startAnalysis()
        order.verify(articleAnalyzer).analyzeArticle(article);
        order.verify(saveArticle).saveArticle(article);      // après completeAnalysis()
        order.verifyNoMoreInteractions();
    }

    // --- analyzer lève une exception ---

    @Test
    void execute_analyzerThrows_articleIsFailed() {
        RssItem article = anyArticle();
        when(getArticle.getNextPendingArticle()).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenThrow(new RuntimeException("timeout"));

        service.execute();

        assertEquals(AnalysisRequestStatus.FAILED, article.getAnalysisStatus());
    }

    @Test
    void execute_analyzerThrows_savesInOrder() {
        RssItem article = anyArticle();
        when(getArticle.getNextPendingArticle()).thenReturn(Optional.of(article));
        when(articleAnalyzer.analyzeArticle(article)).thenThrow(new RuntimeException("timeout"));

        service.execute();

        InOrder order = inOrder(saveArticle, articleAnalyzer);
        order.verify(saveArticle).saveArticle(article);      // après startAnalysis()
        order.verify(articleAnalyzer).analyzeArticle(article);
        order.verify(saveArticle).saveArticle(article);      // après failAnalysis()
        order.verifyNoMoreInteractions();
    }
}
