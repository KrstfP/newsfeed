package com.krstf.newsfeed.port.outbound.repository;

import java.util.Date;
import java.util.List;

public record FullArticleDto(
        String id,
        String title,
        String content,
        String url,
        Date publishedAt,
        String sourceId,
        String source,
        List<String> categories,
        String analysisRequestStatus
) {
}
