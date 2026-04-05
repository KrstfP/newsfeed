package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ClusterEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SpringMongoClusterRepository extends MongoRepository<ClusterEntity, String> {
    List<ClusterEntity> findAllByUserIdAndCreatedAtGreaterThanEqual(String userId, Instant since);
    Optional<ClusterEntity> findFirstByArticleIdsContaining(String articleId);
    void deleteAllByUserId(String userId);
}
