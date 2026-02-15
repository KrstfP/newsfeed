package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.Article;

public interface SaveArticle {
    void saveArticle(Article article);
}
