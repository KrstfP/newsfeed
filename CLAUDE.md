# Newsfeed

Agrégateur de flux RSS avec analyse IA géopolitique/militaire. Architecture hexagonale (ports & adapters) en monorepo.

## Structure du projet

```
srv/   → Backend Spring Boot (Java 21)
front/ → Frontend Vue 3 + TypeScript
```

## Backend (`srv/`)

**Stack :** Spring Boot 3.5, Java 21, MongoDB, Spring AI (Mistral), Firebase Auth

**Architecture hexagonale :**
- `domain/models/` — entités métier (`RssItem`, `RssFeedSource`, `AnalysisRequest`)
- `port/inbound/` — use cases (interfaces)
- `port/outbound/` — ports sortants (repository, notifier, analyzer)
- `application/services/` — implémentation des use cases
- `adapter/inbound/rest/` — REST controllers (`/api/*`)
- `adapter/outbound/repository/mongo/` — persistance MongoDB
- `adapter/outbound/repository/rss/` — chargement RSS (jsoup + Rome)
- `adapter/outbound/mistral/` — appels Mistral via Spring AI

**Lancer le backend :**
```bash
cd srv && ./mvnw spring-boot:run
```

**Tests :**
```bash
cd srv && ./mvnw test
```

## Frontend (`front/`)

**Stack :** Vue 3, TypeScript, Vite (rolldown), Naive UI

**Architecture hexagonale (miroir du backend) :**
- `domain/` — modèles (`Article`)
- `ports/` — interfaces (`ArticleRepository`, `RequestAnalysis`)
- `application/` — use cases (`GetArticles`, `RequestAnalysis`)
- `adapters/` — implémentation HTTP (`NewsfeedArticleRepository` → `http://localhost:8080`)
- `components/` — composants Vue

**Lancer le frontend :**
```bash
cd front && npm install && npm run dev   # http://localhost:5173
```

**Build :**
```bash
cd front && npm run build
```

## Authentification

Firebase Auth côté backend (`FirebaseAuthenticationFilter`). Routes `/app/**` sont publiques, tout le reste (`/api/*`) requiert un token Firebase valide.

## Fonctionnement global

1. `LoadArticlesUseCaseService` se déclenche toutes les 60 minutes pour charger les articles depuis les sources RSS (stockées en MongoDB)
2. Les articles sont dédupliqués et sauvegardés, puis une notification est envoyée
3. Sur demande, `AnalyzeArticleUseCaseService` appelle Mistral pour générer une analyse géopolitique structurée
4. Le frontend consomme `GET /api/articles` et `GET /api/article/{id}/analyze`

## Variables d'environnement

Le fichier `.env` (non versionné) contient les secrets nécessaires (clé Mistral, config Firebase, URI MongoDB).
