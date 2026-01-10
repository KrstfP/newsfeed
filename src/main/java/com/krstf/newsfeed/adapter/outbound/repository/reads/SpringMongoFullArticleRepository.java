package com.krstf.newsfeed.adapter.outbound.repository.reads;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringMongoFullArticleRepository extends MongoRepository<FullArticleEntity, String> {
}
