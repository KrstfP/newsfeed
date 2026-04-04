package com.krstf.newsfeed.domain.models;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArticleClusterTest {

    private RssItem articleWithVector(float[] vector) {
        RssItem item = new RssItem(UUID.randomUUID(), "titre", "contenu",
                "http://example.com", new java.util.Date(), UUID.randomUUID(), "source", "user1");
        item.setSemanticVector(vector);
        return item;
    }

    private ArticleCluster clusterWithCentroid(float[] centroid) {
        return new ArticleCluster(UUID.randomUUID(), null, null, centroid,
                new ArrayList<>(), new ArrayList<>(), Instant.now(), "user1");
    }

    // --- create ---

    @Test
    void create_centroidIsArticleVector() {
        float[] vector = {1f, 0f, 0f};
        RssItem article = articleWithVector(vector);

        ArticleCluster cluster = ArticleCluster.create(article, "user1");

        assertArrayEquals(vector, cluster.getCentroid());
    }

    @Test
    void create_articleIdIsInList() {
        RssItem article = articleWithVector(new float[]{1f, 0f});

        ArticleCluster cluster = ArticleCluster.create(article, "user1");

        assertTrue(cluster.getArticleIds().contains(article.getId()));
        assertEquals(1, cluster.getArticleIds().size());
    }

    @Test
    void create_userIdIsSet() {
        RssItem article = articleWithVector(new float[]{1f, 0f});

        ArticleCluster cluster = ArticleCluster.create(article, "user42");

        assertEquals("user42", cluster.getUserId());
    }

    @Test
    void create_nullVector_centroidIsNull() {
        RssItem article = articleWithVector(null);

        ArticleCluster cluster = ArticleCluster.create(article, "user1");

        assertNull(cluster.getCentroid());
    }

    // --- matches ---

    @Test
    void matches_identicalVectors_returnsTrue() {
        float[] vector = {1f, 0f, 0f};
        ArticleCluster cluster = clusterWithCentroid(vector);

        assertTrue(cluster.matches(new float[]{1f, 0f, 0f}, 0.99f));
    }

    @Test
    void matches_orthogonalVectors_returnsFalse() {
        ArticleCluster cluster = clusterWithCentroid(new float[]{1f, 0f});

        assertFalse(cluster.matches(new float[]{0f, 1f}, 0.5f));
    }

    @Test
    void matches_similarityExactlyAtThreshold_returnsTrue() {
        // cosine similarity de [1,1] et [1,0] = 1/sqrt(2) ≈ 0.7071
        ArticleCluster cluster = clusterWithCentroid(new float[]{1f, 1f});
        float threshold = 1f / (float) Math.sqrt(2);

        assertTrue(cluster.matches(new float[]{1f, 0f}, threshold - 1e-6f));
    }

    @Test
    void matches_nullCentroid_returnsFalse() {
        ArticleCluster cluster = clusterWithCentroid(null);

        assertFalse(cluster.matches(new float[]{1f, 0f}, 0.5f));
    }

    @Test
    void matches_nullVector_returnsFalse() {
        ArticleCluster cluster = clusterWithCentroid(new float[]{1f, 0f});

        assertFalse(cluster.matches(null, 0.5f));
    }

    @Test
    void matches_zeroVector_returnsFalse() {
        ArticleCluster cluster = clusterWithCentroid(new float[]{1f, 0f});

        // similarité cosinus d'un vecteur zéro = 0.0 → inférieur à tout seuil positif
        assertFalse(cluster.matches(new float[]{0f, 0f}, 0.01f));
    }

    // --- addArticle ---

    @Test
    void addArticle_addsIdToArticleIds() {
        RssItem first = articleWithVector(new float[]{1f, 0f});
        ArticleCluster cluster = ArticleCluster.create(first, "user1");
        RssItem second = articleWithVector(new float[]{0f, 1f});

        cluster.addArticle(second);

        assertTrue(cluster.getArticleIds().contains(second.getId()));
        assertEquals(2, cluster.getArticleIds().size());
    }

    @Test
    void addArticle_recomputesCentroidAsIncrementalMean() {
        RssItem first = articleWithVector(new float[]{1f, 0f});
        ArticleCluster cluster = ArticleCluster.create(first, "user1");
        RssItem second = articleWithVector(new float[]{0f, 1f});

        cluster.addArticle(second);

        // (1*[1,0] + [0,1]) / 2 = [0.5, 0.5]
        assertArrayEquals(new float[]{0.5f, 0.5f}, cluster.getCentroid(), 1e-6f);
    }

    @Test
    void addArticle_threeArticles_centroidIsCorrectMean() {
        RssItem a1 = articleWithVector(new float[]{1f, 0f});
        RssItem a2 = articleWithVector(new float[]{0f, 1f});
        RssItem a3 = articleWithVector(new float[]{1f, 1f});
        ArticleCluster cluster = ArticleCluster.create(a1, "user1");
        cluster.addArticle(a2);

        cluster.addArticle(a3);

        // moyenne de [1,0], [0,1], [1,1] = [2/3, 2/3]
        assertArrayEquals(new float[]{2f / 3f, 2f / 3f}, cluster.getCentroid(), 1e-6f);
    }

    @Test
    void addArticle_nullVector_centroidUnchanged() {
        float[] originalCentroid = {1f, 0f};
        RssItem first = articleWithVector(originalCentroid.clone());
        ArticleCluster cluster = ArticleCluster.create(first, "user1");
        RssItem noVector = articleWithVector(null);

        cluster.addArticle(noVector);

        assertArrayEquals(originalCentroid, cluster.getCentroid(), 1e-6f);
    }
}
