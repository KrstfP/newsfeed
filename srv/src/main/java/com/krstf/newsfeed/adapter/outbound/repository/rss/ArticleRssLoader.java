package com.krstf.newsfeed.adapter.outbound.repository.rss;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleRssLoader implements ArticleLoader {

    private final RssEntryParser parser = new RssEntryParser();

    @Override
    public List<RssItem> loadArticles(RssFeedSource rssFeedSource) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(rssFeedSource.getRssFeedUrl().toURL()));
            return feed.getEntries().stream()
                    .map(e -> parser.toArticle(e, rssFeedSource))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load articles from RSS feed: " + rssFeedSource.getRssFeedUrl(), e);
        }
    }
}
