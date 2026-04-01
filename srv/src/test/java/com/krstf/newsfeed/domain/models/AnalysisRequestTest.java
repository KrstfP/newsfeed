package com.krstf.newsfeed.domain.models;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisRequestTest {

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

    @Test
    void start_transitionsToInProgress() {
        AnalysisRequest request = pendingRequest();
        request.start();
        assertEquals(AnalysisRequestStatus.IN_PROGRESS, request.getStatus());
    }

    @Test
    void complete_transitionsToCompleted() {
        AnalysisRequest request = pendingRequest();
        request.complete();
        assertEquals(AnalysisRequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void fail_transitionsToFailed() {
        AnalysisRequest request = pendingRequest();
        request.fail();
        assertEquals(AnalysisRequestStatus.FAILED, request.getStatus());
    }

    @Test
    void fullHappyPath_pendingToInProgressToCompleted() {
        AnalysisRequest request = pendingRequest();
        request.start();
        request.complete();
        assertEquals(AnalysisRequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void fullFailurePath_pendingToInProgressToFailed() {
        AnalysisRequest request = pendingRequest();
        request.start();
        request.fail();
        assertEquals(AnalysisRequestStatus.FAILED, request.getStatus());
    }
}
