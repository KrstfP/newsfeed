package com.krstf.newsfeed.application.services;

import com.krstf.newsfeed.domain.models.ArticleCluster;
import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummarizer;
import com.krstf.newsfeed.port.outbound.ai.SemanticVectorizer;
import com.krstf.newsfeed.port.outbound.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoadArticlesUseCaseService {
    private static final Logger log = LoggerFactory.getLogger(LoadArticlesUseCaseService.class);
    private static final float CLUSTER_SIMILARITY_THRESHOLD = 0.82f;

    private final GetSource getSource;
    private final ArticleLoader articleLoader;
    private final SaveArticle saveArticle;
    private final GetArticle getArticle;
    private final SemanticVectorizer semanticVectorizer;
    private final GetCluster getCluster;
    private final SaveCluster saveCluster;
    private final GetFullArticle getFullArticle;
    private final ClusterSummarizer clusterSummarizer;

    public LoadArticlesUseCaseService(GetSource getSource, ArticleLoader articleLoader,
                                      SaveArticle saveArticle, GetArticle getArticle,
                                      SemanticVectorizer semanticVectorizer,
                                      GetCluster getCluster, SaveCluster saveCluster,
                                      GetFullArticle getFullArticle, ClusterSummarizer clusterSummarizer) {
        this.getSource = getSource;
        this.articleLoader = articleLoader;
        this.saveArticle = saveArticle;
        this.getArticle = getArticle;
        this.semanticVectorizer = semanticVectorizer;
        this.getCluster = getCluster;
        this.saveCluster = saveCluster;
        this.getFullArticle = getFullArticle;
        this.clusterSummarizer = clusterSummarizer;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 60 * 60 * 1000)
    public void refreshArticles() {
        List<RssFeedSource> rssFeedSources = getSource.getAllSources();

        for (RssFeedSource rssFeedSource : rssFeedSources) {
            List<RssItem> rssItems;
            try {
                rssItems = articleLoader.loadArticles(rssFeedSource);
            } catch (Exception e) {
                log.warn("Failed to load articles from source '{}' ({}): {}",
                        rssFeedSource.getName(), rssFeedSource.getRssFeedUrl(), e.getMessage());
                continue;
            }
            for (RssItem rssItem : rssItems) {
                if (getArticle.getArticleById(rssItem.getId()).isEmpty()) {
                    try {
                        float[] vector = semanticVectorizer.vectorizeText(rssItem.getTitle() + " " + rssItem.getContent());
                        rssItem.setSemanticVector(vector);
                    } catch (Exception e) {
                        log.warn("Failed to vectorize article '{}': {}", rssItem.getId(), e.getMessage());
                    }
                    saveArticle.saveArticle(rssItem);
                    assignToCluster(rssItem);
                }
            }
        }
    }

    private void assignToCluster(RssItem article) {
        if (article.getSemanticVector() == null) {
            return;
        }
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
            log.warn("Failed to assign article '{}' to cluster: {}", article.getId(), e.getMessage());
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
