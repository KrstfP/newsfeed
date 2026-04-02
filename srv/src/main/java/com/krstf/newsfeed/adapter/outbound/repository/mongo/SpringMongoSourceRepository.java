package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringMongoSourceRepository extends MongoRepository<SourceEntity, String> {

    // $ne: "DELETED" matche ACTIVE, null, et les documents sans champ status (legacy)
    @Query("{ 'status': { $ne: 'DELETED' } }")
    List<SourceEntity> findAllActive();

    @Query("{ 'userId': ?0, 'status': { $ne: 'DELETED' } }")
    List<SourceEntity> findAllByUserIdActive(String userId);

    @Query("{ '_id': ?0, 'userId': ?1, 'status': { $ne: 'DELETED' } }")
    Optional<SourceEntity> findByIdAndUserIdActive(String id, String userId);
}
