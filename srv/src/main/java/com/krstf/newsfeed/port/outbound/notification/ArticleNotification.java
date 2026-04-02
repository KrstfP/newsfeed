package com.krstf.newsfeed.port.outbound.notification;

import java.util.UUID;

public record ArticleNotification(
        UUID articleId,
        NotificationObjectType objectType,
        NotificationChangeType changeType,
        String userId,
        String oldValue,
        String newValue
) {}
