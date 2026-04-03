package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssFeedSourceStatus;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSourcesUseCaseServiceTest {

    @Mock GetSource getSource;

    @InjectMocks GetSourcesUseCaseService service;

    private RssFeedSource source(String name, String url, String description) {
        return new RssFeedSource(
                UUID.randomUUID(),
                URI.create(url),
                name,
                description,
                "user1",
                RssFeedSourceStatus.ACTIVE
        );
    }

    @Test
    void getSources_noSources_returnsEmptyList() {
        when(getSource.getSourcesByUser("user1")).thenReturn(List.of());

        List<SourceDto> result = service.getSources("user1");

        assertTrue(result.isEmpty());
    }

    @Test
    void getSources_mapsDtoFieldsCorrectly() {
        RssFeedSource s = source("Le Monde", "https://lemonde.fr/rss", "desc");
        when(getSource.getSourcesByUser("user1")).thenReturn(List.of(s));

        List<SourceDto> result = service.getSources("user1");

        assertEquals(1, result.size());
        SourceDto dto = result.getFirst();
        assertEquals(s.getId().toString(), dto.id());
        assertEquals("Le Monde", dto.name());
        assertEquals("https://lemonde.fr/rss", dto.url());
        assertEquals("desc", dto.description());
    }

    @Test
    void getSources_multipleSources_returnsAll() {
        when(getSource.getSourcesByUser("user1")).thenReturn(List.of(
                source("Source A", "https://a.com/rss", null),
                source("Source B", "https://b.com/rss", null)
        ));

        List<SourceDto> result = service.getSources("user1");

        assertEquals(2, result.size());
    }

    @Test
    void getSources_nullDescription_mappedAsNull() {
        RssFeedSource s = source("Source", "https://example.com/rss", null);
        when(getSource.getSourcesByUser("user1")).thenReturn(List.of(s));

        List<SourceDto> result = service.getSources("user1");

        assertNull(result.getFirst().description());
    }
}
