package com.krstf.newsfeed.adapter.outbound.notification;

import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import org.springframework.context.ApplicationEvent;

public class ArticleStatusSpringEvent extends ApplicationEvent {

    private final ArticleNotification notification;

    public ArticleStatusSpringEvent(Object source, ArticleNotification notification) {
        super(source);
        this.notification = notification;
    }

    public ArticleNotification notification() {
        return notification;
    }
}
