package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.Source;

import java.util.List;

public interface SourceGetter {
    List<Source> getSources();
}
