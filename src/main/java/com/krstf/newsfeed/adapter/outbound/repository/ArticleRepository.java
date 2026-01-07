package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.outbound.repository.GetAllArticles;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ArticleRepository implements GetArticle, SaveArticle, GetAllArticles {
    private final Map<UUID, Article> articlesById = new ConcurrentHashMap<>();

    public void saveArticle(Article article) {
        articlesById.put(article.getId(), article);
    }

    public Optional<Article> getArticleById(UUID articleId) {
        return Optional.ofNullable(articlesById.get(articleId));
    }

    @Override
    public List<Article> getAllArticles() {
        return articlesById.values().stream().toList();
    }
}
