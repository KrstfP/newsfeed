package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.LoadArticlesUseCase;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.ArticleDto;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
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

    private final LoadArticlesUseCase loadArticlesUseCase;
    private final RequestArticleAnalysisUseCase requestArticleAnalysisUseCase;

    public ArticleAPI(LoadArticlesUseCase loadArticlesUseCase, RequestArticleAnalysisUseCase requestArticleAnalysisUseCase) {
        this.loadArticlesUseCase = loadArticlesUseCase;
        this.requestArticleAnalysisUseCase = requestArticleAnalysisUseCase;
    }

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleDto>> getArticles() {
        return ResponseEntity.ok(this.loadArticlesUseCase.loadArticles());
    }

    @GetMapping("/article/{articleId}/analyze")
    public ResponseEntity<RequestDto> analyzeArticle(@PathVariable String articleId) {
        return ResponseEntity.ok(this.requestArticleAnalysisUseCase.requestAnalysis(UUID.fromString(articleId)));
    }
}
