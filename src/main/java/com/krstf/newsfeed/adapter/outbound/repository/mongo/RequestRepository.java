package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestEntity;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.entity.RequestStatus;
import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.port.outbound.repository.AnalysisRequestQueue;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequestRepository implements AnalysisRequestQueue {
    private final SpringMongoRequestRepository springMongoRequestRepository;
    private final EntityMapper entityMapper;

    public RequestRepository(SpringMongoRequestRepository springMongoRequestRepository, EntityMapper entityMapper) {
        this.springMongoRequestRepository = springMongoRequestRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public AnalysisRequest add(String articleId) {

        RequestEntity savedEntity = springMongoRequestRepository.save(new RequestEntity(articleId));
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AnalysisRequest> getNextPendingRequest() {
        return springMongoRequestRepository.findFirstByStatusOrderByUpdatedAtAsc(RequestStatus.PENDING)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<AnalysisRequest> findByArticleId(UUID articleId) {
        return springMongoRequestRepository.findById(articleId.toString()).map(entityMapper::toDomain);
    }

    @Override
    public AnalysisRequest save(AnalysisRequest analysisRequest) {
        RequestEntity entity = springMongoRequestRepository.save(entityMapper.toEntity(analysisRequest));
        return entityMapper.toDomain(entity);
    }
}
