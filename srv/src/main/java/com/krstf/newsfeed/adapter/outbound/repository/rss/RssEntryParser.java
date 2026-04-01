package com.krstf.newsfeed.adapter.outbound.repository.rss;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import org.jsoup.Jsoup;

import java.util.Date;
import java.util.List;

class RssEntryParser {

    RssItem toArticle(SyndEntry entry, RssFeedSource source) {
        RssItem rssItem = new RssItem(
                entry.getTitle(),
                Jsoup.parse(extractContent(entry)).text(),
                entry.getLink(),
                extractPublishedDate(entry),
                source.getId(),
                source.getName(),
                source.getUserId()
        );
        rssItem.setCategories(extractCategories(entry));
        return rssItem;
    }

    String extractContent(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().getFirst().getValue();
        }
        if (entry.getDescription() != null) {
            return entry.getDescription().getValue();
        }
        return "";
    }

    Date extractPublishedDate(SyndEntry entry) {
        if (entry.getPublishedDate() != null) {
            return entry.getPublishedDate();
        }
        if (entry.getUpdatedDate() != null) {
            return entry.getUpdatedDate();
        }
        return new Date();
    }

    List<String> extractCategories(SyndEntry entry) {
        return entry.getCategories().stream().map(SyndCategory::getName).toList();
    }
}
