package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.adapter.outbound.repository.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.outbound.repository.GetAllArticles;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ArticleRepository implements GetArticle, SaveArticle, GetAllArticles {

    private final SpringMongoArticleRepository springMongoArticleRepository;
    private final EntityMapper entityMapper;

    private final Map<UUID, Article> articlesById = new ConcurrentHashMap<>();

    public ArticleRepository(SpringMongoArticleRepository springMongoArticleRepository, EntityMapper entityMapper) {
        this.springMongoArticleRepository = springMongoArticleRepository;
        this.entityMapper = entityMapper;
    }

    public void saveArticle(Article article) {
        springMongoArticleRepository.save(entityMapper.toEntity(article));
    }

    public Optional<Article> getArticleById(UUID articleId) {
        return springMongoArticleRepository.findById(articleId.toString()).map(entityMapper::toDomain);
    }

    @Override
    public List<Article> getAllArticles() {
        return springMongoArticleRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }
}
