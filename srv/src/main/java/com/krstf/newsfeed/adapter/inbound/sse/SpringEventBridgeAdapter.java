package com.krstf.newsfeed.adapter.inbound.sse;

import com.krstf.newsfeed.adapter.outbound.notification.ArticleStatusSpringEvent;
import com.krstf.newsfeed.port.outbound.notification.ArticleStatusChangeListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SpringEventBridgeAdapter {

    private final List<ArticleStatusChangeListener> listeners;

    SpringEventBridgeAdapter(List<ArticleStatusChangeListener> listeners) {
        this.listeners = listeners;
    }

    @EventListener
    public void onSpringEvent(ArticleStatusSpringEvent event) {
        listeners.forEach(l -> l.onStatusChanged(event.notification()));
    }
}
