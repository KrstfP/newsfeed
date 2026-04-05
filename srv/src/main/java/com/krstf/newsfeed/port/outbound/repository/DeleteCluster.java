package com.krstf.newsfeed.port.outbound.repository;

public interface DeleteCluster {
    void deleteAllByUserId(String userId);
}
