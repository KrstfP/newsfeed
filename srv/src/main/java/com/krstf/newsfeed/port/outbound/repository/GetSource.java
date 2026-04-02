package com.krstf.newsfeed.port.outbound.repository;

import com.krstf.newsfeed.domain.models.RssFeedSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetSource {
    /** Toutes les sources ACTIVE (tous utilisateurs) — utilisé par le scheduler. */
    List<RssFeedSource> getAllSources();

    /** Sources ACTIVE de l'utilisateur — utilisé par le endpoint GET /api/sources. */
    List<RssFeedSource> getSourcesByUser(String userId);

    /** Source ACTIVE par id + userId — utilisé par le use case de suppression. */
    Optional<RssFeedSource> getSourceById(UUID id, String userId);
}
