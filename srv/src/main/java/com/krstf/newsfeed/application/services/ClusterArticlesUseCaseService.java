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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ClusterArticlesUseCaseService implements ClusterArticlesUseCase, ArticleStatusChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterArticlesUseCaseService.class);

    private final float clusterSimilarityThreshold;

    private final GetArticle getArticle;
    private final GetCluster getCluster;
    private final SaveCluster saveCluster;
    private final DeleteCluster deleteCluster;
    private final GetFullArticle getFullArticle;
    private final ClusterSummarizer clusterSummarizer;

    public ClusterArticlesUseCaseService(@Value("${newsfeed.cluster.similarity-threshold}") float clusterSimilarityThreshold,
                                         GetArticle getArticle,
                                         GetCluster getCluster,
                                         SaveCluster saveCluster,
                                         DeleteCluster deleteCluster,
                                         GetFullArticle getFullArticle,
                                         ClusterSummarizer clusterSummarizer) {
        this.clusterSimilarityThreshold = clusterSimilarityThreshold;
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
        ArticleCluster cluster = getArticle.getArticleById(notification.articleId())
                .map(this::assignToCluster)
                .orElse(null);
        if (cluster != null) updateSummaryAndSave(cluster);
    }

    @Override
    public void rebuildClusters(String userId) {
        log.info("Rebuilding clusters for user '{}'", userId);
        deleteCluster.deleteAllByUserId(userId);
        Set<ArticleCluster> uniqueClusters = new HashSet<>(); // HashSet pour l'unicité
        List<RssItem> articles = getArticle.getAllByUserId(userId);
        for (RssItem article : articles) {
            ArticleCluster cluster = assignToCluster(article);
            if (cluster != null) {
                uniqueClusters.add(cluster);
            }
        }
        uniqueClusters.forEach(cluster -> updateSummaryAndSave(cluster));
        log.info("Rebuilt clusters for user '{}': processed {} articles", userId, articles.size());
    }

    private ArticleCluster assignToCluster(RssItem article) {
        if (article.getSemanticVector() == null) return null;
        try {
            LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            List<ArticleCluster> weekClusters = getCluster.getByUserId(article.getUserId(), weekStart);

            Optional<ArticleCluster> match = weekClusters.stream()
                    .filter(c -> c.matches(article.getSemanticVector(), clusterSimilarityThreshold))
                    .findFirst();

            ArticleCluster cluster;
            if (match.isPresent()) {
                cluster = match.get();
                cluster.addArticle(article);
            } else {
                cluster = ArticleCluster.create(article, article.getUserId());
            }
            return cluster;
        } catch (Exception e) {
            log.warn("Failed to assign article '{}' to cluster", article.getId(), e);
        }
        return null;
    }

    private void updateSummaryAndSave(ArticleCluster cluster) {
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
        } finally {
            saveCluster.save(cluster);
        }
    }
}
