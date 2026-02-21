package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SourceRepository implements GetSource {
    private final SpringMongoSourceRepository springMongoSourceRepository;
    private final EntityMapper entityMapper;

    public SourceRepository(SpringMongoSourceRepository springMongoSourceRepository, EntityMapper entityMapper) {
        this.springMongoSourceRepository = springMongoSourceRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<RssFeedSource> getAllSources() {
        return springMongoSourceRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }
}
