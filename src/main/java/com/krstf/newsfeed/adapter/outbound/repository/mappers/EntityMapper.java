package com.krstf.newsfeed.adapter.outbound.repository.mappers;

import com.krstf.newsfeed.adapter.outbound.repository.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.entity.SourceEntity;
import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.domain.models.Source;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {
    public Source toDomain(SourceEntity entity) {
        return new Source(
                entity.getRssFeedUrl(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public SourceEntity toEntity(Source source) {
        return new SourceEntity(
                source.getId().toString(),
                source.getRssFeedUrl(),
                source.getName(),
                source.getDescription()
        );
    }

    public Article toDomain(ArticleEntity entity) {
        return new Article(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId(),
                entity.getSourceName()
        );
    }

    public ArticleEntity toEntity(Article article) {
        return new ArticleEntity(
                article.getId().toString(),
                article.getTitle(),
                article.getContent(),
                article.getUrl(),
                article.getPublishedAt(),
                article.getSourceId(),
                article.getSourceName(),
                article.getCategories()
        );
    }

}
