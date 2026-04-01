package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringMongoArticleRepository extends MongoRepository<ArticleEntity, String> {
    Optional<ArticleEntity> findFirstByAnalysisStatusOrderByUpdatedAtAsc(String analysisStatus);
}
