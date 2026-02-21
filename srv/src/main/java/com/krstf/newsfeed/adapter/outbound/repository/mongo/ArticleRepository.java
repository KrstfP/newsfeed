package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ArticleRepository implements GetArticle, SaveArticle {

    private final SpringMongoArticleRepository springMongoArticleRepository;
    private final EntityMapper entityMapper;

    private final Map<UUID, RssItem> articlesById = new ConcurrentHashMap<>();

    public ArticleRepository(SpringMongoArticleRepository springMongoArticleRepository, EntityMapper entityMapper) {
        this.springMongoArticleRepository = springMongoArticleRepository;
        this.entityMapper = entityMapper;
    }

    public void saveArticle(RssItem rssItem) {
        springMongoArticleRepository.save(entityMapper.toEntity(rssItem));
    }

    public Optional<RssItem> getArticleById(UUID articleId) {
        return springMongoArticleRepository.findById(articleId.toString()).map(entityMapper::toDomain);
    }
}
