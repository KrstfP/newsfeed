package com.krstf.newsfeed.port.outbound.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleFiltersTest {

    @Test
    void none_returnsBothFieldsNull() {
        ArticleFilters filters = ArticleFilters.none();

        assertNull(filters.analyzed());
        assertNull(filters.since());
    }

    @Test
    void none_returnsMaxLimit() {
        assertEquals(ArticleFilters.MAX_LIMIT, ArticleFilters.none().limit());
    }

    @Test
    void none_returnsNullPageToken() {
        assertNull(ArticleFilters.none().pageToken());
    }
}
