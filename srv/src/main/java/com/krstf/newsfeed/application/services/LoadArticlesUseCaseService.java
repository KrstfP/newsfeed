package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.notification.Notifier;
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
    private final Notifier notifier;


    public LoadArticlesUseCaseService(GetSource getSource, ArticleLoader articleLoader, SaveArticle saveArticle, GetArticle getArticle, Notifier notifier) {
        this.getSource = getSource;
        this.articleLoader = articleLoader;
        this.saveArticle = saveArticle;
        this.getArticle = getArticle;
        this.notifier = notifier;
    }


    @Scheduled(initialDelay = 0,
            fixedDelay = 60 * 60 * 1000 // 60 minutes
    )
    public void refreshArticles() {
        List<RssFeedSource> rssFeedSources = getSource.getAllSources();

        for (RssFeedSource rssFeedSource : rssFeedSources) {
            System.out.println("Loading article from rssFeedSource...." + rssFeedSource);
            List<RssItem> rssItems = articleLoader.loadArticles(rssFeedSource);
            System.out.println("Articles : " + rssItems.size());

            if (rssItems == null || rssItems.isEmpty()) {
                continue;
            }

            System.out.println("Loaded " + rssItems.size() + " rssItems from rssFeedSource: " + rssFeedSource.getName());
            for (RssItem rssItem : rssItems) {
                if (this.getArticle.getArticleById(rssItem.getId()).isEmpty()) {
                    System.out.println("Saving new rssItem: " + rssItem.getTitle());
                    this.saveArticle.saveArticle(rssItem);
                    this.notifier.notifyNewArticleAvailable(rssItem);
                }
            }
        }
    }
}
