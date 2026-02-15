package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoArticleRepository extends MongoRepository<ArticleEntity, String> {
}
