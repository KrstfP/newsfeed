package com.krstf.newsfeed.port.inbound;

import com.krstf.newsfeed.port.inbound.dto.SourceDto;

import java.util.List;

public interface GetSourcesUseCase {
    List<SourceDto> getSources();
}
