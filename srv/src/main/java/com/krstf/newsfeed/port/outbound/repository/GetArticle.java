package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssItem;

import java.util.Optional;
import java.util.UUID;

public interface GetArticle {
    Optional<RssItem> getArticleById(UUID articleId);
    Optional<RssItem> getNextPendingArticle();
}
