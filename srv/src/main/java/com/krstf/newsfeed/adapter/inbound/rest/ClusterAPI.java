package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.ClusterArticlesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClusterAPI {

    private final ClusterArticlesUseCase clusterArticlesUseCase;
    private final CurrentUser currentUser;

    public ClusterAPI(ClusterArticlesUseCase clusterArticlesUseCase, CurrentUser currentUser) {
        this.clusterArticlesUseCase = clusterArticlesUseCase;
        this.currentUser = currentUser;
    }

    @PostMapping("/clusters/rebuild")
    public ResponseEntity<Void> rebuildClusters() {
        clusterArticlesUseCase.rebuildClusters(currentUser.getUserId());
        return ResponseEntity.ok().build();
    }
}
