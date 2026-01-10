package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.outbound.publish.PublishInGithub;
import com.krstf.newsfeed.port.outbound.repository.AnalysisRequestQueue;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnalyzeArticleUseCaseService {

    private final GetArticle getArticle;
    private final ArticleAnalyzer articleAnalyzer;
    private final PublishInGithub publishInGithub;
    private final AnalysisRequestQueue analysisRequestQueue;

    public AnalyzeArticleUseCaseService(GetArticle getArticle, ArticleAnalyzer articleAnalyzer, PublishInGithub publishInGithub, AnalysisRequestQueue analysisRequestQueue) {
        this.getArticle = getArticle;
        this.articleAnalyzer = articleAnalyzer;
        this.publishInGithub = publishInGithub;
        this.analysisRequestQueue = analysisRequestQueue;
    }

    @Scheduled(fixedDelay = 10_000)
    public void execute() {
        Optional<AnalysisRequest> nextPendingRequest = analysisRequestQueue.getNextPendingRequest();
        if (nextPendingRequest.isEmpty()) {
            return;
        }
        AnalysisRequest request = nextPendingRequest.get();

        request.start();
        request = analysisRequestQueue.save(request);
        Optional<Article> article = getArticle.getArticleById(request.getArticleId());
        if (article.isEmpty()) {
            request.fail();
            analysisRequestQueue.save(request);
            return;
        }

//        publishInGithub.publishAnalysis(article.get(), analysis);
//        request.complete();
//        request = analysisRequestQueue.save(request);
    }
}
