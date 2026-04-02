package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssFeedSourceStatus;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SourceRepository implements GetSource, SaveSource {
    private final SpringMongoSourceRepository springMongoSourceRepository;
    private final EntityMapper entityMapper;

    public SourceRepository(SpringMongoSourceRepository springMongoSourceRepository, EntityMapper entityMapper) {
        this.springMongoSourceRepository = springMongoSourceRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<RssFeedSource> getAllSources() {
        return springMongoSourceRepository.findAllByStatus(RssFeedSourceStatus.ACTIVE.name())
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public List<RssFeedSource> getSourcesByUser(String userId) {
        return springMongoSourceRepository.findAllByUserIdAndStatus(userId, RssFeedSourceStatus.ACTIVE.name())
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Optional<RssFeedSource> getSourceById(UUID id, String userId) {
        return springMongoSourceRepository
                .findByIdAndUserIdAndStatus(id.toString(), userId, RssFeedSourceStatus.ACTIVE.name())
                .map(entityMapper::toDomain);
    }

    @Override
    public RssFeedSource save(RssFeedSource source) {
        return entityMapper.toDomain(springMongoSourceRepository.save(entityMapper.toEntity(source)));
    }
}
