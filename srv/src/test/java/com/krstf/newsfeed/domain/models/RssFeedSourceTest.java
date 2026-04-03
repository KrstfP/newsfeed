package com.krstf.newsfeed.domain.models;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RssFeedSourceTest {

    @Test
    void newSource_defaultStatusIsActive() {
        RssFeedSource source = new RssFeedSource(
                URI.create("https://example.com/rss"), "Source", "desc", "user1");

        assertEquals(RssFeedSourceStatus.ACTIVE, source.getStatus());
    }

    @Test
    void delete_changesStatusToDeleted() {
        RssFeedSource source = new RssFeedSource(
                URI.create("https://example.com/rss"), "Source", "desc", "user1");

        source.delete();

        assertEquals(RssFeedSourceStatus.DELETED, source.getStatus());
    }

    @Test
    void id_isDerivedDeterministicallyFromUserIdAndUrl() {
        URI url = URI.create("https://example.com/rss");
        RssFeedSource s1 = new RssFeedSource(url, "A", "desc", "user1");
        RssFeedSource s2 = new RssFeedSource(url, "B", "autre desc", "user1");

        assertEquals(s1.getId(), s2.getId());
    }

    @Test
    void id_differsByUserId() {
        URI url = URI.create("https://example.com/rss");
        RssFeedSource s1 = new RssFeedSource(url, "Source", "desc", "user1");
        RssFeedSource s2 = new RssFeedSource(url, "Source", "desc", "user2");

        assertNotEquals(s1.getId(), s2.getId());
    }

    @Test
    void id_differsByUrl() {
        RssFeedSource s1 = new RssFeedSource(URI.create("https://a.com/rss"), "Source", "desc", "user1");
        RssFeedSource s2 = new RssFeedSource(URI.create("https://b.com/rss"), "Source", "desc", "user1");

        assertNotEquals(s1.getId(), s2.getId());
    }

    @Test
    void fullConstructor_preservesProvidedId() {
        UUID id = UUID.randomUUID();
        RssFeedSource source = new RssFeedSource(
                id, URI.create("https://example.com/rss"), "Source", "desc", "user1", RssFeedSourceStatus.DELETED);

        assertEquals(id, source.getId());
        assertEquals(RssFeedSourceStatus.DELETED, source.getStatus());
    }
}
