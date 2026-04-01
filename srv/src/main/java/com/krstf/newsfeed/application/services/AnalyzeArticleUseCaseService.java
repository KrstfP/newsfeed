package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnalyzeArticleUseCaseService {

    private final GetArticle getArticle;
    private final SaveArticle saveArticle;
    private final ArticleAnalyzer articleAnalyzer;

    public AnalyzeArticleUseCaseService(GetArticle getArticle, SaveArticle saveArticle, ArticleAnalyzer articleAnalyzer) {
        this.getArticle = getArticle;
        this.saveArticle = saveArticle;
        this.articleAnalyzer = articleAnalyzer;
    }

    @Scheduled(fixedDelay = 10_000)
    public void execute() {
        Optional<RssItem> nextPending = getArticle.getNextPendingArticle();
        if (nextPending.isEmpty()) {
            return;
        }

        RssItem rssItem = nextPending.get();
        rssItem.startAnalysis();
        saveArticle.saveArticle(rssItem);

        try {
            String analysis = articleAnalyzer.analyzeArticle(rssItem);
            rssItem.completeAnalysis(analysis);
            saveArticle.saveArticle(rssItem);
        } catch (Exception e) {
            rssItem.failAnalysis();
            saveArticle.saveArticle(rssItem);
        }
    }
}
