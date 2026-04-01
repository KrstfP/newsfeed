package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadArticlesUseCaseService {
    private final GetSource getSource;
    private final ArticleLoader articleLoader;
    private final SaveArticle saveArticle;
    private final GetArticle getArticle;

    public LoadArticlesUseCaseService(GetSource getSource, ArticleLoader articleLoader, SaveArticle saveArticle, GetArticle getArticle) {
        this.getSource = getSource;
        this.articleLoader = articleLoader;
        this.saveArticle = saveArticle;
        this.getArticle = getArticle;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 60 * 60 * 1000)
    public void refreshArticles() {
        List<RssFeedSource> rssFeedSources = getSource.getAllSources();

        for (RssFeedSource rssFeedSource : rssFeedSources) {
            List<RssItem> rssItems = articleLoader.loadArticles(rssFeedSource);
            if (rssItems.isEmpty()) {
                continue;
            }
            for (RssItem rssItem : rssItems) {
                if (getArticle.getArticleById(rssItem.getId()).isEmpty()) {
                    saveArticle.saveArticle(rssItem);
                }
            }
        }
    }
}
