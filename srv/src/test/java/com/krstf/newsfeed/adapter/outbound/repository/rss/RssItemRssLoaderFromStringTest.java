package com.krstf.newsfeed.adapter.outbound.repository.rss;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RssItemRssLoaderFromStringTest {

    @Test
    public void parsesRssFromResource() throws Exception {
        ArticleRssLoaderFromString loader = new ArticleRssLoaderFromString();
        RssFeedSource rssFeedSource = new RssFeedSource(new URI("http://example.com/feed"), "Example", "desc");

        List<RssItem> rssItems = loader.loadArticles(rssFeedSource);

        assertThat(rssItems).hasSize(1);
        RssItem a = rssItems.get(0);
        assertThat(a.getTitle()).isEqualTo("Test RssItem");
        assertThat(a.getUrl()).isEqualTo("http://example.com/test-article");
        assertThat(a.getContent()).contains("This is a test article");
        assertThat(a.getSourceName()).isEqualTo("Example");
        assertThat(a.getSourceId()).isEqualTo(rssFeedSource.getId());
    }
}

