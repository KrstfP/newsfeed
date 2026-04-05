package com.krstf.newsfeed.adapter.outbound.mistral;

import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.ai.ArticleAnalyzer;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummarizer;
import com.krstf.newsfeed.port.outbound.ai.ClusterSummary;
import com.krstf.newsfeed.port.outbound.ai.SemanticVectorizer;
import com.krstf.newsfeed.port.outbound.repository.FullArticleDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MistralAgent implements ArticleAnalyzer, SemanticVectorizer, ClusterSummarizer {
    private final MistralAiChatModel chatModel;
    private final MistralAiEmbeddingModel embeddingsModel;

    private static final String USER_PROMPT = """
            Tu es un analyste militaire et géopolitique.
            Ton objectif est de lire un article dont je te donne l'URL et de générer un **résumé détaillé** en **gardant exactement la structure suivante**, peu importe l’article. Chaque section doit apparaître et être titrée **tel quel**.
            
            STRUCTURE DU RÉSUMÉ (ne jamais changer les titres) :
            
            ## RÉFÉRENCE
            - Source / média
            - Date de publication
            - Auteur (si disponible)
            - Lien direct
            - Type d’article : news / analyse / tribune / reportage
            
            ## TL;DR
            - Résumé ultra-court en 2–3 phrases, pour les décideurs pressés
            - 3–5 points clés très synthétiques
            
            ## SYNTHÈSE / TAKEAWAYS
            - 5–10 lignes résumant ce qu’il faut retenir absolument
            - Messages clés pour un briefing décisionnel
            
            ## SUJET PRINCIPAL
            - Titre complet de l’article
            - Résumé du sujet en 2–3 phrases
            
            ## CONTEXTE DÉTAILLÉ
            - Historique du programme ou de l’opération
            - Capacités ou situation actuelle avant ce développement
            - Comparaison internationale si pertinente
            
            ## CONTENU / FAITS DÉTAILLÉS
            - Éléments techniques ou tactiques importants
            - Chiffres, unités, acteurs impliqués
            - Dates, lieux, étapes importantes
            
            ## ANALYSE TACTIQUE ET STRATÉGIQUE
            - Implications sur le combat, les missions ou la posture stratégique
            - Avantages et limites
            - Scénarios possibles d’utilisation
            - Impact sur la doctrine ou planification militaire, politique ou géopolitique
            
            ## RISQUES ET POINTS DE VIGILANCE
            - Sécurité, fiabilité, coût, intégration technique
            - Dépendances ou vulnérabilités
            - Points à suivre dans le futur
            
            ---
            
            **Instructions supplémentaires** :
            - Pas de phrase d'introduction, tu démarres directement ta réponse par le résumé
            - Rédige en style clair, concis mais complet, orienté militaire / stratégique.
            - Tout ce qui est important dans l’article doit apparaître dans la section appropriée.
            - Ne supprime ou ne change **aucun titre de section**, même si certaines informations manquent, indique “Non mentionné” si nécessaire.
            - Si l’article est très technique, détaille les éléments dans la section "CONTENU / FAITS DÉTAILLÉS".
            
            URL de l’article : %s
            
            """;

    public MistralAgent(MistralAiChatModel chatModel, MistralAiEmbeddingModel embeddingsModel) {
        this.chatModel = chatModel;
        this.embeddingsModel = embeddingsModel;
    }


    @Override
    @RateLimiter(name = "mistral")
    public String analyzeArticle(RssItem rssItem) {
        return this.chatModel.call(USER_PROMPT.formatted(rssItem.getUrl()));
    }

    @Override
    @RateLimiter(name = "mistral")
    public float[] vectorizeText(String text) {
        return embeddingsModel.embed(text);
    }

    @Override
    @RateLimiter(name = "mistral")
    public ClusterSummary summarize(List<FullArticleDto> articles) {
        String articlesText = articles.stream()
                .map(a -> "Titre: " + a.title() + "\n" + a.content())
                .collect(java.util.stream.Collectors.joining("\n\n---\n\n"));

        String prompt = CLUSTER_PROMPT.formatted(articlesText);
        String response = chatModel.call(prompt);
        return parseClusterSummary(response);
    }

    private static final String CLUSTER_PROMPT = """
            Tu es un analyste militaire et géopolitique.
            Voici un ensemble d'articles liés au même sujet :
            
            %s
            
            Génère une synthèse du cluster en respectant exactement ce format JSON (sans markdown, sans texte autour) :
            {
              "topic": "3 à 5 mots décrivant le sujet commun",
              "tldr": "Une phrase résumant l'essentiel",
              "keypoints": ["point clé 1", "point clé 2", "point clé 3"]
            }
            """;

    private static ClusterSummary parseClusterSummary(String response) {
        try {
            String cleaned = response.strip();
            String topic = extractJsonField(cleaned, "topic");
            String tldr = extractJsonField(cleaned, "tldr");
            List<String> keypoints = extractJsonArray(cleaned, "keypoints");
            return new ClusterSummary(topic, tldr, keypoints);
        } catch (Exception e) {
            return new ClusterSummary(null, null, List.of());
        }
    }

    private static String stripQuotes(String s) {
        String t = s.strip();
        if (t.startsWith("\"")) t = t.substring(1);
        if (t.endsWith("\"")) t = t.substring(0, t.length() - 1);
        return t;
    }

    private static String extractJsonField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static List<String> extractJsonArray(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\\[([^\\]]+)\\]";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (!m.find()) return List.of();
        String[] items = m.group(1).split(",");
        return java.util.Arrays.stream(items)
                .map(MistralAgent::stripQuotes)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
