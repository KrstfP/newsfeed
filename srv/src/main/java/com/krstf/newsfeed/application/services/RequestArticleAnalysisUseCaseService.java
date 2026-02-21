package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.inbound.dto.RequestMapper;
import com.krstf.newsfeed.port.outbound.notification.Notifier;
import com.krstf.newsfeed.port.outbound.repository.AnalysisRequestQueue;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RequestArticleAnalysisUseCaseService implements RequestArticleAnalysisUseCase {
    private final AnalysisRequestQueue analysisRequestQueue;
    private final GetArticle getArticle;
    private final RequestMapper requestMapper;
    private final Notifier notifier;

    public RequestArticleAnalysisUseCaseService(AnalysisRequestQueue analysisRequestQueue, GetArticle getArticle, RequestMapper requestMapper, Notifier notifier) {
        this.analysisRequestQueue = analysisRequestQueue;
        this.getArticle = getArticle;
        this.requestMapper = requestMapper;
        this.notifier = notifier;
    }


    @Override
    public RequestDto requestAnalysis(UUID articleId) {
        Optional<AnalysisRequest> analysisRequest = analysisRequestQueue.findByArticleId(articleId);
        if (analysisRequest.isPresent()) {
            return requestMapper.toDto(analysisRequest.get());
        }
        RequestDto requestDto = requestMapper.toDto(analysisRequestQueue.add(articleId.toString()));
        Optional<RssItem> article = getArticle.getArticleById(articleId);
        article.ifPresent(notifier::notifyAnalysisRequested);
        return requestDto;
    }
}
