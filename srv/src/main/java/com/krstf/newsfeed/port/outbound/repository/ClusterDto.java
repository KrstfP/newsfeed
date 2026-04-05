package com.krstf.newsfeed.port.outbound.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClusterDto(
        UUID id,
        String topic,
        String tldr,
        List<String> keypoints,
        List<ClusterArticleDto> articles,
        Instant createdAt
) {
    public record ClusterArticleDto(
            UUID id,
            String title,
            String sourceName,
            String content
    ) {}
}
