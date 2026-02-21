package com.krstf.newsfeed.adapter.outbound.repository.reads;

import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.notification.Notifier;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import com.krstf.newsfeed.port.outbound.repository.GetFullArticle;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FullArticleRepository implements Notifier, GetFullArticle {

    private final SpringMongoFullArticleRepository repository;

    public FullArticleRepository(SpringMongoFullArticleRepository repository) {
        this.repository = repository;
    }

    private static FullArticleDto toDto(FullArticleEntity entity) {
        FullArticleDto dto = new FullArticleDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getSourceId(),
                entity.getSourceName(),
                entity.getCategories(),
                entity.getAnalysisRequestStatus().toString(),
                entity.getAnalysis()
        );
        return dto;
    }

    private static FullArticleEntity updateFromArticle(FullArticleEntity entity, RssItem rssItem) {
        entity.setId(rssItem.getId().toString());
        entity.setTitle(rssItem.getTitle());
        entity.setContent(rssItem.getContent());
        entity.setUrl(rssItem.getUrl());
        entity.setPublishedAt(rssItem.getPublishedAt());
        entity.setSourceId(rssItem.getSourceId().toString());
        entity.setSourceName(rssItem.getSourceName());
        entity.setCategories(rssItem.getCategories());
        return entity;
    }

    private static FullArticleEntity fromArticle(RssItem rssItem) {
        FullArticleEntity entity = new FullArticleEntity();
        entity.setId(rssItem.getId().toString());
        entity.setTitle(rssItem.getTitle());
        entity.setContent(rssItem.getContent());
        entity.setUrl(rssItem.getUrl());
        entity.setPublishedAt(rssItem.getPublishedAt());
        entity.setSourceId(rssItem.getSourceId().toString());
        entity.setSourceName(rssItem.getSourceName());
        entity.setCategories(rssItem.getCategories());
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.NOT_REQUESTED);
        return entity;
    }

    @Override
    public void notifyNewArticleAvailable(RssItem rssItem) {
        FullArticleEntity entity = repository.findById(rssItem.getId().toString()).orElse(fromArticle(rssItem));
        updateFromArticle(entity, rssItem);
        repository.save(entity);

    }

    @Override
    public void notifyAnalysisCompleted(RssItem rssItem, String analysis) {
        FullArticleEntity entity = repository.findById(rssItem.getId().toString()).orElse(fromArticle(rssItem));
        updateFromArticle(entity, rssItem);
        entity.setAnalysis(analysis);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.COMPLETED);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisRequested(RssItem rssItem) {
        FullArticleEntity entity = repository.findById(rssItem.getId().toString()).orElse(fromArticle(rssItem));
        updateFromArticle(entity, rssItem);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.PENDING);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisFailed(RssItem rssItem) {
        FullArticleEntity entity = repository.findById(rssItem.getId().toString()).orElse(fromArticle(rssItem));
        updateFromArticle(entity, rssItem);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.FAILED);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisStarted(RssItem rssItem) {
        FullArticleEntity entity = repository.findById(rssItem.getId().toString()).orElse(fromArticle(rssItem));
        updateFromArticle(entity, rssItem);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.IN_PROGRESS);
        repository.save(entity);
    }

    @Override
    public List<FullArticleDto> getFullArticles() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt")).stream().map(FullArticleRepository::toDto).toList();
    }
}
