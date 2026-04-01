package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestArticleAnalysisUseCaseService implements RequestArticleAnalysisUseCase {

    private final GetArticle getArticle;
    private final SaveArticle saveArticle;

    public RequestArticleAnalysisUseCaseService(GetArticle getArticle, SaveArticle saveArticle) {
        this.getArticle = getArticle;
        this.saveArticle = saveArticle;
    }

    @Override
    public RequestDto requestAnalysis(UUID articleId) {
        RssItem article = getArticle.getArticleById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));
        article.requestAnalysis();
        saveArticle.saveArticle(article);
        return new RequestDto(articleId.toString(), article.getAnalysisStatus().name());
    }
}
