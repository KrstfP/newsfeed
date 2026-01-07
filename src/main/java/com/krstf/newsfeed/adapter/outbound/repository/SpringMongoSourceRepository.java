package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.adapter.outbound.repository.entity.SourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoSourceRepository extends MongoRepository<SourceEntity, String> {
}
