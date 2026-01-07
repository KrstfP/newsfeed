package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.inbound.AnalyzeArticleUseCase;
import com.krstf.newsfeed.port.outbound.publish.PublishInGithub;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AnalyzeArticleUseCaseService implements AnalyzeArticleUseCase {

    private final GetArticle getArticle;
    private final ArticleAnalyzer articleAnalyzer;
    private final PublishInGithub publishInGithub;

    public AnalyzeArticleUseCaseService(GetArticle getArticle, ArticleAnalyzer articleAnalyzer, PublishInGithub publishInGithub) {
        this.getArticle = getArticle;
        this.articleAnalyzer = articleAnalyzer;
        this.publishInGithub = publishInGithub;
    }

    @Override
    public String analyzeArticle(UUID articleId) {
        Optional<Article> article = getArticle.getArticleById(articleId);
        String analysis = article
                .map(articleAnalyzer::analyzeArticle)
                .orElse("");
        article.ifPresent(value -> publishInGithub.publishAnalysis(value, analysis));
        return  analysis;
    }
}
