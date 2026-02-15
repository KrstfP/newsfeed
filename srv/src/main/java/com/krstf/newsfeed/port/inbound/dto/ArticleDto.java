package com.krstf.newsfeed.port.inbound.dto;

import com.krstf.newsfeed.domain.models.Source;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ArticleDto(UUID id,
                         String title,
                         String content,
                         String url,
                         Date publishedAt,
                         String source,
                         List<String>categories) {
}
