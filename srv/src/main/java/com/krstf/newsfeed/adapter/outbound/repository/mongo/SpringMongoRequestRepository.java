package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface SpringMongoRequestRepository extends MongoRepository<RequestEntity, String> {
    Optional<RequestEntity>
    findFirstByStatusOrderByUpdatedAtAsc(RequestStatus status);
}
