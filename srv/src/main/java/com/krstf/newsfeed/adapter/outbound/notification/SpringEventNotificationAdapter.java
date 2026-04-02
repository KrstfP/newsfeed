package com.krstf.newsfeed.adapter.outbound.notification;

import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.NotifyArticleStatusChange;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringEventNotificationAdapter implements NotifyArticleStatusChange {

    private final ApplicationEventPublisher publisher;

    public SpringEventNotificationAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void notify(ArticleNotification notification) {
        publisher.publishEvent(new ArticleStatusSpringEvent(this, notification));
    }
}
