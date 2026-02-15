package com.krstf.newsfeed.port.inbound.dto;

import com.krstf.newsfeed.domain.models.Article;

public interface ArticleMapper {
    ArticleDto toDto(Article article);
}
