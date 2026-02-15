package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.outbound.notification.Notifier;
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
    private final AnalysisRequestQueue analysisRequestQueue;
    private final Notifier notifier;

    public AnalyzeArticleUseCaseService(GetArticle getArticle, ArticleAnalyzer articleAnalyzer, AnalysisRequestQueue analysisRequestQueue, Notifier notifier) {
        this.getArticle = getArticle;
        this.articleAnalyzer = articleAnalyzer;
        this.analysisRequestQueue = analysisRequestQueue;
        this.notifier = notifier;
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
        article.ifPresent(notifier::notifyAnalysisStarted);
        if (article.isEmpty()) {
            request.fail();
            analysisRequestQueue.save(request);
            article.ifPresent(notifier::notifyAnalysisFailed);
            return;
        }
        String analysis = articleAnalyzer.analyzeArticle(article.get());
        request.complete();
        request = analysisRequestQueue.save(request);
        article.ifPresent(a -> notifier.notifyAnalysisCompleted(a, analysis));
    }
}
