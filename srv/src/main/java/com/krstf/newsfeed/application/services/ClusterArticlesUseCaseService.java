package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.inbound.ClusterArticlesUseCase;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummarizer;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.notification.ArticleNotification;
import com.krstf.newsfeed.port.outbound.notification.ArticleStatusChangeListener;
import com.krstf.newsfeed.port.outbound.notification.NotificationChangeType;
import com.krstf.newsfeed.port.outbound.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClusterArticlesUseCaseService implements ClusterArticlesUseCase, ArticleStatusChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterArticlesUseCaseService.class);
    private static final float CLUSTER_SIMILARITY_THRESHOLD = 0.80f;

    private final GetArticle getArticle;
    private final GetCluster getCluster;
    private final SaveCluster saveCluster;
    private final DeleteCluster deleteCluster;
    private final GetFullArticle getFullArticle;
    private final ClusterSummarizer clusterSummarizer;

    public ClusterArticlesUseCaseService(GetArticle getArticle,
                                         GetCluster getCluster,
                                         SaveCluster saveCluster,
                                         DeleteCluster deleteCluster,
                                         GetFullArticle getFullArticle,
                                         ClusterSummarizer clusterSummarizer) {
        this.getArticle = getArticle;
        this.getCluster = getCluster;
        this.saveCluster = saveCluster;
        this.deleteCluster = deleteCluster;
        this.getFullArticle = getFullArticle;
        this.clusterSummarizer = clusterSummarizer;
    }

    @Override
    public void onStatusChanged(ArticleNotification notification) {
        if (notification.changeType() != NotificationChangeType.ARTICLE_CREATED) return;
        getArticle.getArticleById(notification.articleId()).ifPresent(this::assignToCluster);
    }

    @Override
    public void rebuildClusters(String userId) {
        log.info("Rebuilding clusters for user '{}'", userId);
        deleteCluster.deleteAllByUserId(userId);
        List<RssItem> articles = getArticle.getAllByUserId(userId);
        for (RssItem article : articles) {
            assignToCluster(article);
        }
        log.info("Rebuilt clusters for user '{}': processed {} articles", userId, articles.size());
    }

    private void assignToCluster(RssItem article) {
        if (article.getSemanticVector() == null) return;
        try {
            LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            List<ArticleCluster> weekClusters = getCluster.getByUserId(article.getUserId(), weekStart);

            Optional<ArticleCluster> match = weekClusters.stream()
                    .filter(c -> c.matches(article.getSemanticVector(), CLUSTER_SIMILARITY_THRESHOLD))
                    .findFirst();

            ArticleCluster cluster;
            if (match.isPresent()) {
                cluster = match.get();
                cluster.addArticle(article);
            } else {
                cluster = ArticleCluster.create(article, article.getUserId());
            }

            updateSummary(cluster);
            saveCluster.save(cluster);
        } catch (Exception e) {
            log.warn("Failed to assign article '{}' to cluster", article.getId(), e);
        }
    }

    private void updateSummary(ArticleCluster cluster) {
        try {
            List<FullArticleDto> articles = getFullArticle.getFullArticles(
                            cluster.getUserId(),
                            ArticleFilters.none()
                    ).articles().stream()
                    .filter(a -> cluster.getArticleIds().contains(java.util.UUID.fromString(a.id())))
                    .toList();

            ClusterSummary summary = clusterSummarizer.summarize(articles);
            cluster.setTopic(summary.topic());
            cluster.setTldr(summary.tldr());
            cluster.setKeypoints(summary.keypoints());
        } catch (Exception e) {
            log.warn("Failed to summarize cluster '{}': {}", cluster.getId(), e.getMessage());
        }
    }
}
