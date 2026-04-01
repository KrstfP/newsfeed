package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.port.inbound.AddSourceUseCase;
import com.krstf.newsfeed.port.inbound.dto.CreateSourceRequest;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import com.krstf.newsfeed.port.outbound.repository.SaveSource;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class AddSourceUseCaseService implements AddSourceUseCase {

    private final SaveSource saveSource;

    public AddSourceUseCaseService(SaveSource saveSource) {
        this.saveSource = saveSource;
    }

    @Override
    public SourceDto addSource(CreateSourceRequest request, String userId) {
        URI uri;
        try {
            uri = URI.create(request.url());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + request.url(), e);
        }

        RssFeedSource source = new RssFeedSource(uri, request.name(), request.description(), userId);
        RssFeedSource saved = saveSource.save(source);
        return new SourceDto(saved.getId().toString(), saved.getName(), saved.getRssFeedUrl().toString(), saved.getDescription());
    }
}
