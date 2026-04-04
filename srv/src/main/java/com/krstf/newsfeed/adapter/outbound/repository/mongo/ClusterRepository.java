package com.krstf.newsfeed.adapter.outbound.repository.mongo;

import com.krstf.newsfeed.adapter.outbound.repository.mongo.mappers.EntityMapper;
import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.port.outbound.repository.GetCluster;
import com.krstf.newsfeed.port.outbound.repository.SaveCluster;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClusterRepository implements GetCluster, SaveCluster {

    private final SpringMongoClusterRepository springMongoClusterRepository;
    private final EntityMapper entityMapper;

    public ClusterRepository(SpringMongoClusterRepository springMongoClusterRepository,
                             EntityMapper entityMapper) {
        this.springMongoClusterRepository = springMongoClusterRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public ArticleCluster save(ArticleCluster cluster) {
        return entityMapper.toDomain(
                springMongoClusterRepository.save(entityMapper.toEntity(cluster))
        );
    }

    @Override
    public Optional<ArticleCluster> getById(UUID id) {
        return springMongoClusterRepository.findById(id.toString())
                .map(entityMapper::toDomain);
    }

    @Override
    public List<ArticleCluster> getByUserId(String userId) {
        return springMongoClusterRepository.findAllByUserId(userId).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ArticleCluster> getByArticleId(UUID articleId) {
        return springMongoClusterRepository.findFirstByArticleIdsContaining(articleId.toString())
                .map(entityMapper::toDomain);
    }
}
