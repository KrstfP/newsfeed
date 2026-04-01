package com.krstf.newsfeed.port.outbound.repository;

import java.util.List;

public interface GetFullArticle {
    List<FullArticleDto> getFullArticles(String userId);
}
