package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.AnalysisRequestStatus;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.RequestArticleAnalysisUseCase;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.NotificationChangeType;
import com.krstf.newsfeed.port.outbound.notification.NotificationObjectType;
import com.krstf.newsfeed.port.outbound.notification.NotifyArticleStatusChange;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestArticleAnalysisUseCaseService implements RequestArticleAnalysisUseCase {

    private final GetArticle getArticle;
    private final SaveArticle saveArticle;
    private final NotifyArticleStatusChange notifyStatusChange;

    public RequestArticleAnalysisUseCaseService(GetArticle getArticle, SaveArticle saveArticle,
                                                NotifyArticleStatusChange notifyStatusChange) {
        this.getArticle = getArticle;
        this.saveArticle = saveArticle;
        this.notifyStatusChange = notifyStatusChange;
    }

    @Override
    public RequestDto requestAnalysis(UUID articleId) {
        RssItem article = getArticle.getArticleById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        if (article.getAnalysisStatus() == AnalysisRequestStatus.IN_PROGRESS) {
            return new RequestDto(articleId.toString(), article.getAnalysisStatus().name());
        }

        AnalysisRequestStatus previousStatus = article.getAnalysisStatus();
        article.requestAnalysis();
        saveArticle.saveArticle(article);

        notifyStatusChange.notify(new ArticleNotification(
                article.getId(),
                NotificationObjectType.ARTICLE,
                NotificationChangeType.ANALYSIS_STATUS_CHANGED,
                article.getUserId(),
                previousStatus.name(),
                AnalysisRequestStatus.PENDING.name()
        ));

        return new RequestDto(articleId.toString(), article.getAnalysisStatus().name());
    }
}
