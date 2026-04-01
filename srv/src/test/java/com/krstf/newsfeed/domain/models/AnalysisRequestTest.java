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

    private AnalysisRequest inProgressRequest() {
        AnalysisRequest request = pendingRequest();
        request.start();
        return request;
    }

    // --- start() ---

    @Test
    void start_fromPending_transitionsToInProgress() {
        AnalysisRequest request = pendingRequest();
        request.start();
        assertEquals(AnalysisRequestStatus.IN_PROGRESS, request.getStatus());
    }

    @Test
    void start_fromInProgress_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        assertThrows(IllegalStateException.class, request::start);
    }

    @Test
    void start_fromCompleted_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.complete();
        assertThrows(IllegalStateException.class, request::start);
    }

    @Test
    void start_fromFailed_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.fail();
        assertThrows(IllegalStateException.class, request::start);
    }

    // --- complete() ---

    @Test
    void complete_fromInProgress_transitionsToCompleted() {
        AnalysisRequest request = inProgressRequest();
        request.complete();
        assertEquals(AnalysisRequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void complete_fromPending_throwsIllegalState() {
        AnalysisRequest request = pendingRequest();
        assertThrows(IllegalStateException.class, request::complete);
    }

    @Test
    void complete_fromCompleted_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.complete();
        assertThrows(IllegalStateException.class, request::complete);
    }

    @Test
    void complete_fromFailed_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.fail();
        assertThrows(IllegalStateException.class, request::complete);
    }

    // --- fail() ---

    @Test
    void fail_fromInProgress_transitionsToFailed() {
        AnalysisRequest request = inProgressRequest();
        request.fail();
        assertEquals(AnalysisRequestStatus.FAILED, request.getStatus());
    }

    @Test
    void fail_fromPending_throwsIllegalState() {
        AnalysisRequest request = pendingRequest();
        assertThrows(IllegalStateException.class, request::fail);
    }

    @Test
    void fail_fromCompleted_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.complete();
        assertThrows(IllegalStateException.class, request::fail);
    }

    @Test
    void fail_fromFailed_throwsIllegalState() {
        AnalysisRequest request = inProgressRequest();
        request.fail();
        assertThrows(IllegalStateException.class, request::fail);
    }

    // --- transitions en chaîne ---

    @Test
    void fullHappyPath_pendingToInProgressToCompleted() {
        AnalysisRequest request = pendingRequest();
        assertEquals(AnalysisRequestStatus.PENDING, request.getStatus());
        request.start();
        assertEquals(AnalysisRequestStatus.IN_PROGRESS, request.getStatus());
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
