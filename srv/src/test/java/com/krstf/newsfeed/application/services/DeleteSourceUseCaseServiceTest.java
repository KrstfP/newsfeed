package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssFeedSourceStatus;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSourceUseCaseServiceTest {

    @Mock GetSource getSource;
    @Mock SaveSource saveSource;

    @InjectMocks DeleteSourceUseCaseService service;

    private RssFeedSource activeSource(UUID id, String userId) {
        return new RssFeedSource(id, URI.create("https://example.com/rss"), "Example", "", userId, RssFeedSourceStatus.ACTIVE);
    }

    // --- source trouvée ---

    @Test
    void deleteSource_found_marksAsDeleted() {
        UUID id = UUID.randomUUID();
        RssFeedSource source = activeSource(id, "user1");
        when(getSource.getSourceById(id, "user1")).thenReturn(Optional.of(source));
        when(saveSource.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.deleteSource(id, "user1");

        ArgumentCaptor<RssFeedSource> captor = ArgumentCaptor.forClass(RssFeedSource.class);
        verify(saveSource).save(captor.capture());
        assertEquals(RssFeedSourceStatus.DELETED, captor.getValue().getStatus());
    }

    @Test
    void deleteSource_found_savesExactlyOnce() {
        UUID id = UUID.randomUUID();
        when(getSource.getSourceById(id, "user1")).thenReturn(Optional.of(activeSource(id, "user1")));
        when(saveSource.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.deleteSource(id, "user1");

        verify(saveSource, times(1)).save(any());
    }

    // --- source introuvable (inexistante ou mauvais userId) ---

    @Test
    void deleteSource_notFound_throwsNoSuchElement() {
        UUID id = UUID.randomUUID();
        when(getSource.getSourceById(id, "user1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.deleteSource(id, "user1"));
    }

    @Test
    void deleteSource_notFound_neverCallsSave() {
        UUID id = UUID.randomUUID();
        when(getSource.getSourceById(id, "user1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.deleteSource(id, "user1"));

        verifyNoInteractions(saveSource);
    }

    @Test
    void deleteSource_wrongUserId_throwsNoSuchElement() {
        UUID id = UUID.randomUUID();
        // getSourceById filtre déjà par userId — renvoie empty si le userId ne correspond pas
        when(getSource.getSourceById(id, "other-user")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.deleteSource(id, "other-user"));
    }
}
