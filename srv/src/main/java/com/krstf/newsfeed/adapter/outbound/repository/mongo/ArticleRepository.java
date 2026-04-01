package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.GetFullArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleRepository implements GetArticle, SaveArticle, GetFullArticle {

    private final SpringMongoArticleRepository springMongoArticleRepository;
    private final EntityMapper entityMapper;

    public ArticleRepository(SpringMongoArticleRepository springMongoArticleRepository, EntityMapper entityMapper) {
        this.springMongoArticleRepository = springMongoArticleRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public void saveArticle(RssItem rssItem) {
        springMongoArticleRepository.save(entityMapper.toEntity(rssItem));
    }

    @Override
    public Optional<RssItem> getArticleById(UUID articleId) {
        return springMongoArticleRepository.findById(articleId.toString()).map(entityMapper::toDomain);
    }

    @Override
    public Optional<RssItem> getNextPendingArticle() {
        return springMongoArticleRepository.findFirstByAnalysisStatusOrderByUpdatedAtAsc("PENDING")
                .map(entityMapper::toDomain);
    }

    @Override
    public List<FullArticleDto> getFullArticles() {
        return springMongoArticleRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))
                .stream()
                .map(entityMapper::toFullArticleDto)
                .toList();
    }
}
