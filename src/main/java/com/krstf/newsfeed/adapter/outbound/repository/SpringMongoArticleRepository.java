package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.adapter.outbound.repository.entity.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoArticleRepository extends MongoRepository<ArticleEntity, String> {
}
