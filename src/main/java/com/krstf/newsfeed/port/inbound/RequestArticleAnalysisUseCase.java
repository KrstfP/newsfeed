package com.krstf.newsfeed.port.inbound;

import com.krstf.newsfeed.port.inbound.dto.RequestDto;

import java.util.UUID;

public interface RequestArticleAnalysisUseCase {
    RequestDto requestAnalysis(UUID articleId);
}
