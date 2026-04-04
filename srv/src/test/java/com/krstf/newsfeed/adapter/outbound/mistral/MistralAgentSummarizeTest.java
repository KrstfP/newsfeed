package com.krstf.newsfeed.adapter.outbound.mistral;

import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MistralAgentSummarizeTest {

    @Mock MistralAiChatModel chatModel;
    @Mock MistralAiEmbeddingModel embeddingsModel;

    @InjectMocks MistralAgent agent;

    private FullArticleDto anyArticle() {
        return new FullArticleDto(
                UUID.randomUUID().toString(), "Titre test", "Contenu de l'article",
                "http://example.com", new Date(), UUID.randomUUID().toString(),
                "Source", List.of(), "COMPLETED", null
        );
    }

    private static final String VALID_JSON = """
            {
              "topic": "conflit Proche-Orient",
              "tldr": "Une phrase résumant l'essentiel.",
              "keypoints": ["frappe aérienne", "cessez-le-feu", "Rafah"]
            }
            """;

    // --- parsing valide ---

    @Test
    void summarize_validJson_returnsCorrectTopic() {
        when(chatModel.call(anyString())).thenReturn(VALID_JSON);

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertEquals("conflit Proche-Orient", summary.topic());
    }

    @Test
    void summarize_validJson_returnsCorrectTldr() {
        when(chatModel.call(anyString())).thenReturn(VALID_JSON);

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertEquals("Une phrase résumant l'essentiel.", summary.tldr());
    }

    @Test
    void summarize_validJson_returnsAllKeypoints() {
        when(chatModel.call(anyString())).thenReturn(VALID_JSON);

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertEquals(List.of("frappe aérienne", "cessez-le-feu", "Rafah"), summary.keypoints());
    }

    // --- robustesse du parsing ---

    @Test
    void summarize_missingField_returnsNullForThatField() {
        when(chatModel.call(anyString())).thenReturn("""
                { "tldr": "résumé", "keypoints": ["k1"] }
                """);

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertNull(summary.topic());
        assertEquals("résumé", summary.tldr());
    }

    @Test
    void summarize_emptyKeypoints_returnsEmptyList() {
        when(chatModel.call(anyString())).thenReturn("""
                { "topic": "sujet", "tldr": "résumé", "keypoints": [] }
                """);

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertTrue(summary.keypoints().isEmpty());
    }

    @Test
    void summarize_malformedResponse_returnsFallback() {
        when(chatModel.call(anyString())).thenReturn("réponse invalide non JSON");

        ClusterSummary summary = agent.summarize(List.of(anyArticle()));

        assertNull(summary.topic());
        assertNull(summary.tldr());
        assertTrue(summary.keypoints().isEmpty());
    }

    @Test
    void summarize_emptyArticleList_callsChatModel() {
        when(chatModel.call(anyString())).thenReturn(VALID_JSON);

        ClusterSummary summary = agent.summarize(List.of());

        assertNotNull(summary);
    }
}
