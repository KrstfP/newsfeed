package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.AnalysisEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoAnalysisRepository extends MongoRepository<AnalysisEntity, String> {
}
