package com.krstf.newsfeed.adapter.outbound.repository.mongo.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClusterEntityTest {

    @Test
    void getUpdatedAt_initiallyNull() {
        ClusterEntity entity = new ClusterEntity(
                UUID.randomUUID().toString(), "topic", "tldr",
                new float[]{0.1f}, List.of("k1"), List.of(),
                "user1", Instant.now()
        );

        assertNull(entity.getUpdatedAt());
    }
}
