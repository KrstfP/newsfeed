package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.port.inbound.dto.CreateSourceRequest;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import com.krstf.newsfeed.port.outbound.repository.SaveSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSourceUseCaseServiceTest {

    @Mock SaveSource saveSource;

    @InjectMocks AddSourceUseCaseService service;

    private RssFeedSource savedSource(String url, String name, String description) {
        return new RssFeedSource(java.net.URI.create(url), name, description, "test-user");
    }

    // --- URL invalide ---

    @Test
    void addSource_invalidUrl_throwsIllegalArgument() {
        CreateSourceRequest request = new CreateSourceRequest("not a valid url", "Source", null);

        assertThrows(IllegalArgumentException.class, () -> service.addSource(request, "test-user"));
        verifyNoInteractions(saveSource);
    }

    // --- URL valide ---

    @Test
    void addSource_validRequest_callsSaveSource() {
        CreateSourceRequest request = new CreateSourceRequest("https://example.com/feed", "Le Monde", "desc");
        when(saveSource.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.addSource(request, "test-user");

        verify(saveSource).save(any(RssFeedSource.class));
    }

    @Test
    void addSource_validRequest_returnsDtoWithCorrectFields() {
        CreateSourceRequest request = new CreateSourceRequest("https://example.com/feed", "Le Monde", "description");
        when(saveSource.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SourceDto result = service.addSource(request, "test-user");

        assertEquals("Le Monde", result.name());
        assertEquals("https://example.com/feed", result.url());
        assertEquals("description", result.description());
        assertNotNull(result.id());
    }

    @Test
    void addSource_withoutDescription_succeeds() {
        CreateSourceRequest request = new CreateSourceRequest("https://example.com/feed", "Le Monde", null);
        when(saveSource.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SourceDto result = service.addSource(request, "test-user");

        assertEquals("Le Monde", result.name());
        assertNull(result.description());
    }
}
