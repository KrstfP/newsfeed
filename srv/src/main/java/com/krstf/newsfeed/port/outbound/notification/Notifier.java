package com.krstf.newsfeed.port.outbound.notification;

import com.krstf.newsfeed.domain.models.RssItem;

public interface Notifier {
    void notifyNewArticleAvailable(RssItem rssItem);

    void notifyAnalysisCompleted(RssItem rssItem, String analysis);

    void notifyAnalysisRequested(RssItem rssItem);

    void notifyAnalysisFailed(RssItem rssItem);

    void notifyAnalysisStarted(RssItem rssItem);
}
