package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.ArticleCluster;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetCluster {
    Optional<ArticleCluster> getById(UUID id);
    List<ArticleCluster> getByUserId(String userId, LocalDate since);
    Optional<ArticleCluster> getByArticleId(UUID articleId);
}
