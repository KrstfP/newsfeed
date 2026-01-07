package com.krstf.newsfeed.port.outbound.publish;

import com.krstf.newsfeed.domain.models.Article;

public interface PublishInGithub {
    void publishAnalysis(Article article, String analysis);
}
