package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadArticlesUseCaseService {
    private static final Logger log = LoggerFactory.getLogger(LoadArticlesUseCaseService.class);
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
            List<RssItem> rssItems;
            try {
                rssItems = articleLoader.loadArticles(rssFeedSource);
            } catch (Exception e) {
                log.warn("Failed to load articles from source '{}' ({}): {}",
                        rssFeedSource.getName(), rssFeedSource.getRssFeedUrl(), e.getMessage());
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
