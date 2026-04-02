package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.repository.ArticleFilters;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import com.krstf.newsfeed.port.outbound.repository.GetFullArticle;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<List<FullArticleDto>> getArticles(
            @RequestParam(required = false) Boolean analyzed,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since
    ) {
        return ResponseEntity.ok(
                getFullArticle.getFullArticles(currentUser.getUserId(), new ArticleFilters(analyzed, since))
        );
    }

    @GetMapping("/article/{articleId}/analyze")
    public ResponseEntity<RequestDto> analyzeArticle(@PathVariable String articleId) {
        return ResponseEntity.ok(requestArticleAnalysisUseCase.requestAnalysis(UUID.fromString(articleId)));
    }
}
