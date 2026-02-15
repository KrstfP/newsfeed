package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.Article;

import java.util.Optional;
import java.util.UUID;

public interface GetArticle {
    Optional<Article> getArticleById(UUID articleId);
}
