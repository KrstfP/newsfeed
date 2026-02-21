package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssItem;

public interface SaveArticle {
    void saveArticle(RssItem rssItem);
}
