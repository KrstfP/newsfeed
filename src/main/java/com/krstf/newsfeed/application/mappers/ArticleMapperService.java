package com.krstf.newsfeed.application.mappers;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.inbound.dto.ArticleDto;
import com.krstf.newsfeed.port.inbound.dto.ArticleMapper;
import org.springframework.stereotype.Service;

@Service
public class ArticleMapperService implements ArticleMapper {

    @Override
    public ArticleDto toDto(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUrl(),
                article.getPublishedAt(),
                article.getSource().getName(),
                article.getCategories()
        );
    }
}
