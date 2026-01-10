package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.inbound.dto.RequestMapper;
import com.krstf.newsfeed.port.outbound.repository.AnalysisRequestQueue;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RequestArticleAnalysisUseCaseService implements RequestArticleAnalysisUseCase {
    private final AnalysisRequestQueue analysisRequestQueue;
    private final RequestMapper requestMapper;

    public RequestArticleAnalysisUseCaseService(AnalysisRequestQueue analysisRequestQueue, RequestMapper requestMapper) {
        this.analysisRequestQueue = analysisRequestQueue;
        this.requestMapper = requestMapper;
    }


    @Override
    public RequestDto requestAnalysis(UUID articleId) {
        Optional<AnalysisRequest> analysisRequest = analysisRequestQueue.findByArticleId(articleId);
        if (analysisRequest.isPresent()) {
            return requestMapper.toDto(analysisRequest.get());
        }
        return requestMapper.toDto(analysisRequestQueue.add(articleId.toString()));
    }
}
