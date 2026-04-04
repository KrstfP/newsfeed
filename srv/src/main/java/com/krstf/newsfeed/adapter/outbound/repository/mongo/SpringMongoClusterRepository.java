package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.ClusterEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpringMongoClusterRepository extends MongoRepository<ClusterEntity, String> {
    List<ClusterEntity> findAllByUserId(String userId);
    Optional<ClusterEntity> findFirstByArticleIdsContaining(String articleId);
}
