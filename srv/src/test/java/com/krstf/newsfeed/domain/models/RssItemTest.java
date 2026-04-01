package com.krstf.newsfeed.domain.models;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RssItemTest {

    private RssItem anyItem() {
        return new RssItem(UUID.randomUUID(), "titre", "contenu", "http://example.com", new Date(), UUID.randomUUID(), "source", "test-user");
    }

    @Test
    void requestAnalysis_setsToPending() {
        RssItem item = anyItem();
        item.requestAnalysis();
        assertEquals(AnalysisRequestStatus.PENDING, item.getAnalysisStatus());
    }

    @Test
    void startAnalysis_setsToInProgress() {
        RssItem item = anyItem();
        item.startAnalysis();
        assertEquals(AnalysisRequestStatus.IN_PROGRESS, item.getAnalysisStatus());
    }

    @Test
    void completeAnalysis_setsToCompleted() {
        RssItem item = anyItem();
        item.completeAnalysis("résumé");
        assertEquals(AnalysisRequestStatus.COMPLETED, item.getAnalysisStatus());
    }

    @Test
    void completeAnalysis_storesAnalysisText() {
        RssItem item = anyItem();
        item.completeAnalysis("résumé détaillé");
        assertEquals("résumé détaillé", item.getAnalysis());
    }

    @Test
    void failAnalysis_setsToFailed() {
        RssItem item = anyItem();
        item.failAnalysis();
        assertEquals(AnalysisRequestStatus.FAILED, item.getAnalysisStatus());
    }

    @Test
    void failAnalysis_analysisRemainsNull() {
        RssItem item = anyItem();
        item.failAnalysis();
        assertNull(item.getAnalysis());
    }

    @Test
    void defaultStatus_isNotRequested() {
        RssItem item = anyItem();
        assertEquals(AnalysisRequestStatus.NOT_REQUESTED, item.getAnalysisStatus());
    }

    @Test
    void defaultAnalysis_isNull() {
        RssItem item = anyItem();
        assertNull(item.getAnalysis());
    }
}
