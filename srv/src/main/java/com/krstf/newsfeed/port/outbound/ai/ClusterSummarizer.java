package com.krstf.newsfeed.port.outbound.ai;

import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;

import java.util.List;

public interface ClusterSummarizer {
    ClusterSummary summarize(List<FullArticleDto> articles);
}
