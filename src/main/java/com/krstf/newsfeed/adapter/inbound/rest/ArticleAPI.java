package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.adapter.outbound.repository.reads.FullArticleRepository;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
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

    private final FullArticleRepository fullArticleRepository;
    private final RequestArticleAnalysisUseCase requestArticleAnalysisUseCase;

    public ArticleAPI(FullArticleRepository fullArticleRepository, RequestArticleAnalysisUseCase requestArticleAnalysisUseCase) {
        this.fullArticleRepository = fullArticleRepository;
        this.requestArticleAnalysisUseCase = requestArticleAnalysisUseCase;
    }

    @GetMapping("/articles")
    public ResponseEntity<List<FullArticleDto>> getArticles() {
        return ResponseEntity.ok(this.fullArticleRepository.getFullArticles());
    }

    @GetMapping("/article/{articleId}/analyze")
    public ResponseEntity<RequestDto> analyzeArticle(@PathVariable String articleId) {
        return ResponseEntity.ok(this.requestArticleAnalysisUseCase.requestAnalysis(UUID.fromString(articleId)));
    }
}
