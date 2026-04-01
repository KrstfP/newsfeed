package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestArticleAnalysisUseCaseServiceTest {

    @Mock GetArticle getArticle;
    @Mock SaveArticle saveArticle;

    @InjectMocks RequestArticleAnalysisUseCaseService service;

    private RssItem articleWithStatus(UUID id, AnalysisRequestStatus status) {
        RssItem article = new RssItem(id, "titre", "contenu", "http://example.com", new Date(), UUID.randomUUID(), "source");
        article.setAnalysisStatus(status);
        return article;
    }

    // --- article inexistant ---

    @Test
    void requestAnalysis_articleNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(getArticle.getArticleById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.requestAnalysis(id));
        verifyNoInteractions(saveArticle);
    }

    // --- analyse en cours : on ne touche pas au statut ---

    @Test
    void requestAnalysis_alreadyInProgress_returnsCurrentStatusWithoutSaving() {
        UUID id = UUID.randomUUID();
        RssItem article = articleWithStatus(id, AnalysisRequestStatus.IN_PROGRESS);
        when(getArticle.getArticleById(id)).thenReturn(Optional.of(article));

        RequestDto result = service.requestAnalysis(id);

        assertEquals("IN_PROGRESS", result.status());
        assertEquals(AnalysisRequestStatus.IN_PROGRESS, article.getAnalysisStatus());
        verifyNoInteractions(saveArticle);
    }

    // --- tous les autres statuts : on relance (PENDING, NOT_REQUESTED, COMPLETED, FAILED) ---

    @ParameterizedTest
    @EnumSource(value = AnalysisRequestStatus.class, names = {"NOT_REQUESTED", "PENDING", "COMPLETED", "FAILED"})
    void requestAnalysis_anyOtherStatus_setsArticleToPendingAndSaves(AnalysisRequestStatus initialStatus) {
        UUID id = UUID.randomUUID();
        RssItem article = articleWithStatus(id, initialStatus);
        when(getArticle.getArticleById(id)).thenReturn(Optional.of(article));

        RequestDto result = service.requestAnalysis(id);

        assertEquals("PENDING", result.status());
        assertEquals(AnalysisRequestStatus.PENDING, article.getAnalysisStatus());
        verify(saveArticle).saveArticle(article);
    }

    @Test
    void requestAnalysis_success_returnsDtoWithArticleId() {
        UUID id = UUID.randomUUID();
        RssItem article = articleWithStatus(id, AnalysisRequestStatus.NOT_REQUESTED);
        when(getArticle.getArticleById(id)).thenReturn(Optional.of(article));

        RequestDto result = service.requestAnalysis(id);

        assertEquals(id.toString(), result.articleId());
    }
}
