package com.krstf.newsfeed.port.outbound.notification;

public interface NotifyArticleStatusChange {
    void notify(ArticleNotification notification);
}
