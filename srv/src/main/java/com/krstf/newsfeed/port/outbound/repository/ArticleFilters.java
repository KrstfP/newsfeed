package com.krstf.newsfeed.port.outbound.repository;

import java.time.LocalDate;

public record ArticleFilters(Boolean analyzed, LocalDate since, int limit, String pageToken) {
    public static final int MAX_LIMIT = 1000;

    public static ArticleFilters none() {
        return new ArticleFilters(null, null, MAX_LIMIT, null);
    }
}
