package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.ArticleCluster;

public interface SaveCluster {
    ArticleCluster save(ArticleCluster cluster);
}
