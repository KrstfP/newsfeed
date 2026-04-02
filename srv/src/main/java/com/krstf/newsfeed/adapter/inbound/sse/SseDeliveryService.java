package com.krstf.newsfeed.adapter.inbound.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.ArticleStatusChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
class SseDeliveryService implements ArticleStatusChangeListener {

    private static final Logger log = LoggerFactory.getLogger(SseDeliveryService.class);

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    SseDeliveryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    void register(String userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
    }

    @Scheduled(fixedDelay = 25_000)
    void sendKeepalive() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("keepalive"));
            } catch (IOException e) {
                emitters.remove(userId);
                emitter.complete();
            }
        });
    }

    @Override
    public void onStatusChanged(ArticleNotification notification) {
        SseEmitter emitter = emitters.get(notification.userId());
        if (emitter == null) return;

        try {
            Map<String, String> payload = Map.of(
                    "articleId", notification.articleId().toString(),
                    "objectType", notification.objectType().name(),
                    "changeType", notification.changeType().name(),
                    "oldValue", notification.oldValue(),
                    "newValue", notification.newValue()
            );
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(payload), MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.warn("SSE send failed for user {}, removing emitter", notification.userId());
            emitters.remove(notification.userId());
            emitter.complete();
        }
    }
}
