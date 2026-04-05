package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.ClusterArticlesUseCase;
import com.krstf.newsfeed.port.outbound.repository.ClusterDto;
import com.krstf.newsfeed.port.outbound.repository.ClusterDto.ClusterArticleDto;
import com.krstf.newsfeed.port.outbound.repository.GetArticle;
import com.krstf.newsfeed.port.outbound.repository.GetCluster;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClusterAPI {

    private final ClusterArticlesUseCase clusterArticlesUseCase;
    private final GetCluster getCluster;
    private final GetArticle getArticle;
    private final CurrentUser currentUser;

    public ClusterAPI(ClusterArticlesUseCase clusterArticlesUseCase,
                      GetCluster getCluster,
                      GetArticle getArticle,
                      CurrentUser currentUser) {
        this.clusterArticlesUseCase = clusterArticlesUseCase;
        this.getCluster = getCluster;
        this.getArticle = getArticle;
        this.currentUser = currentUser;
    }

    @GetMapping("/clusters")
    public ResponseEntity<List<ClusterDto>> getClusters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since
    ) {
        String userId = currentUser.getUserId();
        LocalDate effectiveSince = since != null ? since : LocalDate.now().with(DayOfWeek.MONDAY);

        List<ArticleCluster> clusters = getCluster.getByUserId(userId, effectiveSince);

        Map<UUID, RssItem> articlesById = getArticle.getAllByUserId(userId).stream()
                .collect(Collectors.toMap(RssItem::getId, a -> a));

        List<ClusterDto> dtos = clusters.stream()
                .map(c -> toDto(c, articlesById))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/clusters/rebuild")
    public ResponseEntity<Void> rebuildClusters() {
        clusterArticlesUseCase.rebuildClusters(currentUser.getUserId());
        return ResponseEntity.ok().build();
    }

    private static ClusterDto toDto(ArticleCluster cluster, Map<UUID, RssItem> articlesById) {
        List<ClusterArticleDto> articles = cluster.getArticleIds().stream()
                .filter(articlesById::containsKey)
                .map(id -> {
                    RssItem a = articlesById.get(id);
                    return new ClusterArticleDto(a.getId(), a.getTitle(), a.getSourceName(), a.getContent());
                })
                .toList();
        return new ClusterDto(
                cluster.getId(),
                cluster.getTopic(),
                cluster.getTldr(),
                cluster.getKeypoints(),
                articles,
                cluster.getCreatedAt()
        );
    }
}
