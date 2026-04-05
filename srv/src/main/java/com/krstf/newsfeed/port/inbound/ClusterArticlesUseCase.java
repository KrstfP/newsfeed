package com.krstf.newsfeed.port.inbound;

public interface ClusterArticlesUseCase {
    void rebuildClusters(String userId);
}
