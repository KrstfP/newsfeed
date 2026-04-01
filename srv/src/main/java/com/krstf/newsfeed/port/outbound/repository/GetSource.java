package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssFeedSource;

import java.util.List;

public interface GetSource {
    List<RssFeedSource> getAllSources();
    List<RssFeedSource> getSourcesByUser(String userId);
}
