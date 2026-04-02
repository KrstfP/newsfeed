package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.NotificationChangeType;
import com.krstf.newsfeed.port.outbound.notification.NotificationObjectType;
import com.krstf.newsfeed.port.outbound.notification.NotifyArticleStatusChange;
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
    private final NotifyArticleStatusChange notifyStatusChange;

    public AnalyzeArticleUseCaseService(GetArticle getArticle, SaveArticle saveArticle,
                                        ArticleAnalyzer articleAnalyzer,
                                        NotifyArticleStatusChange notifyStatusChange) {
        this.getArticle = getArticle;
        this.saveArticle = saveArticle;
        this.articleAnalyzer = articleAnalyzer;
        this.notifyStatusChange = notifyStatusChange;
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
        notify(rssItem, AnalysisRequestStatus.PENDING, AnalysisRequestStatus.IN_PROGRESS);

        try {
            String analysis = articleAnalyzer.analyzeArticle(rssItem);
            rssItem.completeAnalysis(analysis);
            saveArticle.saveArticle(rssItem);
            notify(rssItem, AnalysisRequestStatus.IN_PROGRESS, AnalysisRequestStatus.COMPLETED);
        } catch (Exception e) {
            rssItem.failAnalysis();
            saveArticle.saveArticle(rssItem);
            notify(rssItem, AnalysisRequestStatus.IN_PROGRESS, AnalysisRequestStatus.FAILED);
        }
    }

    private void notify(RssItem article, AnalysisRequestStatus oldStatus, AnalysisRequestStatus newStatus) {
        notifyStatusChange.notify(new ArticleNotification(
                article.getId(),
                NotificationObjectType.ARTICLE,
                NotificationChangeType.ANALYSIS_STATUS_CHANGED,
                article.getUserId(),
                oldStatus.name(),
                newStatus.name()
        ));
    }
}
