package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.Article;

public interface ArticleAnalyzer {
    String analyzeArticle(Article article);
}
