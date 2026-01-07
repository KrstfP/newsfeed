package com.krstf.newsfeed.port.inbound;

import java.util.UUID;

public interface AnalyzeArticleUseCase {
    String analyzeArticle(UUID articleId);
}
