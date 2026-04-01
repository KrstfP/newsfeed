package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.port.inbound.GetSourcesUseCase;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import com.krstf.newsfeed.port.outbound.repository.GetSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSourcesUseCaseService implements GetSourcesUseCase {

    private final GetSource getSource;

    public GetSourcesUseCaseService(GetSource getSource) {
        this.getSource = getSource;
    }

    @Override
    public List<SourceDto> getSources(String userId) {
        return getSource.getSourcesByUser(userId).stream()
                .map(s -> new SourceDto(
                        s.getId().toString(),
                        s.getName(),
                        s.getRssFeedUrl().toString(),
                        s.getDescription()
                ))
                .toList();
    }
}
