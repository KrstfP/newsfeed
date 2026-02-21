package com.krstf.newsfeed.adapter.outbound.repository.rss;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Test/dev helper: charge un RSS depuis le fichier `rss-sample.xml` présent dans les resources.
 * Utile pour les tests unitaires ou pour démarrer l'app en local sans accès réseau.
 */
@Service
public class ArticleRssLoaderFromString implements ArticleLoader {
    private static final String RESOURCE_NAME = "rss-sample.xml";

    public ArticleRssLoaderFromString() {
        // loader sans argument : lit toujours la ressource
    }

    @Override
    public List<RssItem> loadArticles(RssFeedSource rssFeedSource) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME);
             XmlReader reader = new XmlReader(in)) {

            if (in == null) {
                throw new RuntimeException("Resource '" + RESOURCE_NAME + "' not found on classpath");
            }

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(reader);

            return feed.getEntries()
                    .stream()
                    .map(e -> toArticle(e, rssFeedSource))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load articles from RSS feed (from resource)", e);
        }
    }

    private RssItem toArticle(SyndEntry entry, RssFeedSource rssFeedSource) {
        RssItem rssItem = new RssItem(
                entry.getTitle(),
                Jsoup.parse(extractContent(entry)).text(),
                entry.getLink(),
                extractPublishedDate(entry),
                rssFeedSource.getId(),
                rssFeedSource.getName()
        );
        rssItem.setCategories(extractCategories(entry));
        return rssItem;
    }

    private List<String> extractCategories(SyndEntry entry) {
        List<SyndCategory> categories = entry.getCategories();
        return categories.stream().map(SyndCategory::getName).toList();
    }

    private String extractContent(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().getFirst().getValue();
        }
        if (entry.getDescription() != null) {
            return entry.getDescription().getValue();
        }
        return "";
    }

    private Date extractPublishedDate(SyndEntry entry) {
        if (entry.getPublishedDate() != null) {
            return entry.getPublishedDate();
        }
        if (entry.getUpdatedDate() != null) {
            return entry.getUpdatedDate();
        }
        return new Date(); // fallback
    }
}
