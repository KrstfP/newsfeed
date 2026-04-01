package com.krstf.newsfeed.port.inbound;

import com.krstf.newsfeed.port.inbound.dto.CreateSourceRequest;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;

public interface AddSourceUseCase {
    SourceDto addSource(CreateSourceRequest request);
}
