package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.inbound.AnalyzeArticleUseCase;
import com.krstf.newsfeed.port.inbound.LoadArticlesUseCase;
import com.krstf.newsfeed.port.inbound.dto.ArticleDto;
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
    private final AnalyzeArticleUseCase analyzeArticleUseCase;

    public ArticleAPI(LoadArticlesUseCase loadArticlesUseCase, AnalyzeArticleUseCase analyzeArticleUseCase) {
        this.loadArticlesUseCase = loadArticlesUseCase;
        this.analyzeArticleUseCase = analyzeArticleUseCase;
    }

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleDto>> getArticles() {
        return ResponseEntity.ok(this.loadArticlesUseCase.loadArticles());
    }

    @GetMapping("/article/{articleId}/analyze")
    public ResponseEntity<String> analyzeArticle(@PathVariable String articleId) {
        String analysis = this.analyzeArticleUseCase.analyzeArticle(UUID.fromString(articleId));
        return ResponseEntity.ok(analysis);
    }
}
