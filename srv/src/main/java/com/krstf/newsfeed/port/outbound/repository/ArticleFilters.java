package com.krstf.newsfeed.port.outbound.repository;

import java.time.LocalDate;

public record ArticleFilters(Boolean analyzed, LocalDate since) {
    public static ArticleFilters none() {
        return new ArticleFilters(null, null);
    }
}
