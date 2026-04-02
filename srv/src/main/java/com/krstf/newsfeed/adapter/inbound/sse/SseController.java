package com.krstf.newsfeed.adapter.inbound.sse;

import com.krstf.newsfeed.adapter.inbound.rest.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class SseController {

    private static final long TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);

    private final SseDeliveryService sseDeliveryService;
    private final CurrentUser currentUser;

    public SseController(SseDeliveryService sseDeliveryService, CurrentUser currentUser) {
        this.sseDeliveryService = sseDeliveryService;
        this.currentUser = currentUser;
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        sseDeliveryService.register(currentUser.getUserId(), emitter);
        return emitter;
    }
}
