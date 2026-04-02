package com.krstf.newsfeed.port.outbound.notification;

public interface ArticleStatusChangeListener {
    void onStatusChanged(ArticleNotification notification);
}
