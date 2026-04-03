package com.krstf.newsfeed.port.outbound.ai;

import com.krstf.newsfeed.domain.models.RssItem;

public interface ArticleAnalyzer {
    String analyzeArticle(RssItem rssItem);
}
