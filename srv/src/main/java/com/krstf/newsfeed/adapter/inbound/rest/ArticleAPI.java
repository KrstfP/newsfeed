package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import com.krstf.newsfeed.port.outbound.repository.GetFullArticle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ArticleAPI {

    private final GetFullArticle getFullArticle;
    private final RequestArticleAnalysisUseCase requestArticleAnalysisUseCase;
    private final CurrentUser currentUser;

    public ArticleAPI(GetFullArticle getFullArticle, RequestArticleAnalysisUseCase requestArticleAnalysisUseCase, CurrentUser currentUser) {
        this.getFullArticle = getFullArticle;
        this.requestArticleAnalysisUseCase = requestArticleAnalysisUseCase;
        this.currentUser = currentUser;
    }

    @GetMapping("/articles")
    public ResponseEntity<List<FullArticleDto>> getArticles() {
        return ResponseEntity.ok(getFullArticle.getFullArticles(currentUser.getUserId()));
    }

    @GetMapping("/article/{articleId}/analyze")
    public ResponseEntity<RequestDto> analyzeArticle(@PathVariable String articleId) {
        return ResponseEntity.ok(requestArticleAnalysisUseCase.requestAnalysis(UUID.fromString(articleId)));
    }
}
