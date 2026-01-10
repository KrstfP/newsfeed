package com.krstf.newsfeed.port.outbound.notification;

import com.krstf.newsfeed.domain.models.Article;

public interface Notifier {
    void notifyNewArticleAvailable(Article article);

    void notifyAnalysisCompleted(Article article, String analysis);

    void notifyAnalysisRequested(Article article);

    void notifyAnalysisFailed(Article article);

    void notifyAnalysisStarted(Article article);
}
