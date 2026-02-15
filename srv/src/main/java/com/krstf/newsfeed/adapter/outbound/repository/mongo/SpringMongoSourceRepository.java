package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.SourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoSourceRepository extends MongoRepository<SourceEntity, String> {
}
