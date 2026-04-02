package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleRepository implements GetArticle, SaveArticle, GetFullArticle {

    private final SpringMongoArticleRepository springMongoArticleRepository;
    private final EntityMapper entityMapper;
    private final MongoTemplate mongoTemplate;

    public ArticleRepository(SpringMongoArticleRepository springMongoArticleRepository,
                             EntityMapper entityMapper,
                             MongoTemplate mongoTemplate) {
        this.springMongoArticleRepository = springMongoArticleRepository;
        this.entityMapper = entityMapper;
        this.mongoTemplate = mongoTemplate;
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
    public List<FullArticleDto> getFullArticles(String userId, ArticleFilters filters) {
        Criteria criteria = Criteria.where("userId").is(userId);

        if (filters.analyzed() != null) {
            if (filters.analyzed()) {
                criteria = criteria.and("analysisStatus").is("COMPLETED");
            } else {
                criteria = criteria.and("analysisStatus").ne("COMPLETED");
            }
        }

        if (filters.since() != null) {
            Date sinceDate = Date.from(filters.since().atStartOfDay(ZoneOffset.UTC).toInstant());
            criteria = criteria.and("publishedAt").gte(sinceDate);
        }

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "publishedAt"));
        return mongoTemplate.find(query, ArticleEntity.class)
                .stream()
                .map(entityMapper::toFullArticleDto)
                .toList();
    }
}
