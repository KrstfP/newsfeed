package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.port.inbound.DeleteSourceUseCase;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import com.krstf.newsfeed.port.outbound.repository.SaveSource;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteSourceUseCaseService implements DeleteSourceUseCase {

    private final GetSource getSource;
    private final SaveSource saveSource;

    public DeleteSourceUseCaseService(GetSource getSource, SaveSource saveSource) {
        this.getSource = getSource;
        this.saveSource = saveSource;
    }

    @Override
    public void deleteSource(UUID sourceId, String userId) {
        RssFeedSource source = getSource.getSourceById(sourceId, userId)
                .orElseThrow(() -> new NoSuchElementException("Source not found: " + sourceId));
        source.delete();
        saveSource.save(source);
    }
}
