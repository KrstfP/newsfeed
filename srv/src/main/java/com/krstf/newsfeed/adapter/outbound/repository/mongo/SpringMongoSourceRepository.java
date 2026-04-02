package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpringMongoSourceRepository extends MongoRepository<SourceEntity, String> {
    List<SourceEntity> findAllByStatus(String status);
    List<SourceEntity> findAllByUserIdAndStatus(String userId, String status);
    Optional<SourceEntity> findByIdAndUserIdAndStatus(String id, String userId, String status);
}
