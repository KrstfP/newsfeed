package com.krstf.newsfeed.port.outbound.repository;

import java.util.List;

public record PagedArticlesResponse(List<FullArticleDto> articles, String nextPageToken) {
}
