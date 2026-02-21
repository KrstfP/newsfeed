package com.krstf.newsfeed.adapter.outbound.mistral;

import com.krstf.newsfeed.domain.models.RssItem;
import com.krstf.newsfeed.port.outbound.repository.ArticleAnalyzer;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class MistralAgent implements ArticleAnalyzer {
    private final MistralAiChatModel chatModel;

    private static final String userPrompt = """
            Tu es un analyste militaire et géopolitique.
            Ton objectif est de lire un rssItem dont je te donne l'URL et de générer un **résumé détaillé** en **gardant exactement la structure suivante**, peu importe l’rssItem. Chaque section doit apparaître et être titrée **tel quel**.
            
            STRUCTURE DU RÉSUMÉ (ne jamais changer les titres) :
            
            
            
            ## RÉFÉRENCE
            - Source / média
            - Date de publication
            - Auteur (si disponible)
            - Lien direct
            - Type d’rssItem : news / analyse / tribune / reportage
            
            
            ## SYNTHÈSE / TAKEAWAYS
            - 5–10 lignes résumant ce qu’il faut retenir absolument
            - Messages clés pour un briefing décisionnel
            
            ## SUJET PRINCIPAL
            - Titre complet de l’rssItem
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
            - Tout ce qui est important dans l’rssItem doit apparaître dans la section appropriée.
            - Ne supprime ou ne change **aucun titre de section**, même si certaines informations manquent, indique “Non mentionné” si nécessaire.
            - Si l’rssItem est très technique, détaille les éléments dans la section "CONTENU / FAITS DÉTAILLÉS".
            
            URL de l’rssItem : %s
            
            """;

    public MistralAgent(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @Override
    public String analyzeArticle(RssItem rssItem) {
        return this.chatModel.call(userPrompt.formatted(rssItem.getUrl()));
    }
}
