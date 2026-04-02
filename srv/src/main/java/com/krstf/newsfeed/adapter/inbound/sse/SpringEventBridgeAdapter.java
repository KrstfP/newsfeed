package com.krstf.newsfeed.adapter.inbound.sse;

import com.krstf.newsfeed.adapter.outbound.notification.ArticleStatusSpringEvent;
import com.krstf.newsfeed.port.outbound.notification.ArticleStatusChangeListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class SpringEventBridgeAdapter {

    private final ArticleStatusChangeListener listener;

    SpringEventBridgeAdapter(ArticleStatusChangeListener listener) {
        this.listener = listener;
    }

    @EventListener
    public void onSpringEvent(ArticleStatusSpringEvent event) {
        listener.onStatusChanged(event.notification());
    }
}
