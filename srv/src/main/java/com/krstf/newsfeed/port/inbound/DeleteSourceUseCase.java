package com.krstf.newsfeed.port.inbound;

import java.util.UUID;

public interface DeleteSourceUseCase {
    void deleteSource(UUID sourceId, String userId);
}
