package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssFeedSource;

public interface SaveSource {
    RssFeedSource save(RssFeedSource source);
}
