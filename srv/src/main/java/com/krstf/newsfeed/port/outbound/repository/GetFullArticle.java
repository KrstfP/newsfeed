package com.krstf.newsfeed.port.outbound.repository;

public interface GetFullArticle {
    PagedArticlesResponse getFullArticles(String userId, ArticleFilters filters);
}
