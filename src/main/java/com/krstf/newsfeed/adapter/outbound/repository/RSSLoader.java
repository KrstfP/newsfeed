package com.krstf.newsfeed.adapter.outbound.repository;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.domain.models.Source;
import com.krstf.newsfeed.port.outbound.repository.ArticleLoader;
import com.rometools.rome.feed.synd.SyndCategory;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.util.Date;
import java.util.List;

@Service
public class RSSLoader implements ArticleLoader {
    @Override
    public List<Article> loadArticles(Source source) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(source.getRssFeedUrl().toURL()));

            return feed.getEntries()
                    .stream()
                    .map(e -> toArticle(e, source))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load articles from RSS feed: " + source.getRssFeedUrl(),
                    e
            );
        }
    }

    private Article toArticle(SyndEntry entry, Source source) {
        Article article = new Article(
                entry.getTitle(),
                extractContent(entry),
                entry.getLink(),
                extractPublishedDate(entry),
                source
        );
        article.setCategories(extractCategories(entry));
        return article;
    }

    private List<String> extractCategories(SyndEntry entry) {
        List<SyndCategory> categories = entry.getCategories();
        return categories.stream().map(c -> c.getName()).toList();
    }

    private String extractContent(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().get(0).getValue();
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
