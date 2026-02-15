package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.AnalysisRequest;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisRequestQueue {
    AnalysisRequest add(String articleId);

    Optional<AnalysisRequest> getNextPendingRequest();

    Optional<AnalysisRequest> findByArticleId(UUID articleId);

    AnalysisRequest save(AnalysisRequest analysisRequest);


}
