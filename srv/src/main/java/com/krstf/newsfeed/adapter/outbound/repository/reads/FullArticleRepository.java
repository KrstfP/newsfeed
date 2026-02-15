package com.krstf.newsfeed.adapter.outbound.repository.reads;

import com.krstf.newsfeed.domain.models.Article;
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
                entity.getAnalysisRequestStatus().toString()
        );
        return dto;
    }

    private static FullArticleEntity updateFromArticle(FullArticleEntity entity, Article article) {
        entity.setId(article.getId().toString());
        entity.setTitle(article.getTitle());
        entity.setContent(article.getContent());
        entity.setUrl(article.getUrl());
        entity.setPublishedAt(article.getPublishedAt());
        entity.setSourceId(article.getSourceId().toString());
        entity.setSourceName(article.getSourceName());
        entity.setCategories(article.getCategories());
        return entity;
    }

    private static FullArticleEntity fromArticle(Article article) {
        FullArticleEntity entity = new FullArticleEntity();
        entity.setId(article.getId().toString());
        entity.setTitle(article.getTitle());
        entity.setContent(article.getContent());
        entity.setUrl(article.getUrl());
        entity.setPublishedAt(article.getPublishedAt());
        entity.setSourceId(article.getSourceId().toString());
        entity.setSourceName(article.getSourceName());
        entity.setCategories(article.getCategories());
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.NOT_REQUESTED);
        return entity;
    }

    @Override
    public void notifyNewArticleAvailable(Article article) {
        FullArticleEntity entity = repository.findById(article.getId().toString()).orElse(fromArticle(article));
        updateFromArticle(entity, article);
        repository.save(entity);

    }

    @Override
    public void notifyAnalysisCompleted(Article article, String analysis) {
        FullArticleEntity entity = repository.findById(article.getId().toString()).orElse(fromArticle(article));
        updateFromArticle(entity, article);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.COMPLETED);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisRequested(Article article) {
        FullArticleEntity entity = repository.findById(article.getId().toString()).orElse(fromArticle(article));
        updateFromArticle(entity, article);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.PENDING);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisFailed(Article article) {
        FullArticleEntity entity = repository.findById(article.getId().toString()).orElse(fromArticle(article));
        updateFromArticle(entity, article);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.FAILED);
        repository.save(entity);
    }

    @Override
    public void notifyAnalysisStarted(Article article) {
        FullArticleEntity entity = repository.findById(article.getId().toString()).orElse(fromArticle(article));
        updateFromArticle(entity, article);
        entity.setAnalysisRequestStatus(AnalysisRequestStatus.IN_PROGRESS);
        repository.save(entity);
    }

    @Override
    public List<FullArticleDto> getFullArticles() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt")).stream().map(FullArticleRepository::toDto).toList();
    }
}
