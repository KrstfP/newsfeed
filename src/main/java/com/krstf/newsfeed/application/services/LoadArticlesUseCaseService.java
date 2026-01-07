package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.domain.models.Source;
import com.krstf.newsfeed.port.inbound.LoadArticlesUseCase;
import com.krstf.newsfeed.port.inbound.RefreshArticlesUseCase;
import com.krstf.newsfeed.port.inbound.dto.ArticleDto;
import com.krstf.newsfeed.port.inbound.dto.ArticleMapper;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.krstf.newsfeed.port.outbound.repository.GetAllArticles;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadArticlesUseCaseService implements LoadArticlesUseCase, RefreshArticlesUseCase {
    private final GetSource getSource;
    private final ArticleLoader articleLoader;
    private final GetAllArticles getAllArticles;
    private final SaveArticle saveArticle;
    private final ArticleMapper articleMapper;


    public LoadArticlesUseCaseService(GetSource getSource, ArticleLoader articleLoader, GetAllArticles getAllArticles, SaveArticle saveArticle, ArticleMapper articleMapper) {
        this.getSource = getSource;
        this.articleLoader = articleLoader;
        this.getAllArticles = getAllArticles;
        this.saveArticle = saveArticle;
        this.articleMapper = articleMapper;
    }


    @Override
    public List<ArticleDto> loadArticles() {
        return this.getAllArticles.getAllArticles().stream().map(articleMapper::toDto).toList();
    }

    @Scheduled(initialDelay = 0,
            fixedDelay = 60 * 60 * 1000 // 60 minutes
    )
    @Override
    public void refreshArticles() {
        List<Source> sources = getSource.getAllSources();

        for (Source source : sources) {
            List<Article> articles = articleLoader.loadArticles(source);

            if (articles == null || articles.isEmpty()) {
                continue;
            }

            for (Article article : articles) {
                this.saveArticle.saveArticle(article);
            }
        }
    }
}
