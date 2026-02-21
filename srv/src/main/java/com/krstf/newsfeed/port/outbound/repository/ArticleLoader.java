package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;

import java.util.List;

public interface ArticleLoader {
    List<RssItem> loadArticles(RssFeedSource rssFeedSource);
}
