package com.krstf.newsfeed.port.inbound;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.inbound.dto.ArticleDto;

import java.util.List;

public interface LoadArticlesUseCase {
    List<ArticleDto> loadArticles();
}
