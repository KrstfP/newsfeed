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
- `domain/models/` — entités métier : `RssItem`, `RssFeedSource`, `AnalysisRequestStatus`
- `port/inbound/` — use cases (interfaces) : `RequestArticleAnalysisUseCase`, `GetSourcesUseCase`, `AddSourceUseCase`
- `port/outbound/repository/` — ports sortants : `GetArticle`, `SaveArticle`, `GetFullArticle`, `GetSource`, `SaveSource`, `ArticleAnalyzer`, `ArticleLoader`
- `application/services/` — implémentation des use cases
- `adapter/inbound/rest/` — REST controllers (`/api/*`) + `CurrentUser`
- `adapter/outbound/repository/mongo/` — persistance MongoDB (2 collections : `articles`, `sources`)
- `adapter/outbound/repository/rss/` — chargement RSS (Rome + jsoup)
- `adapter/outbound/mistral/` — appels Mistral via Spring AI

**Modèles du domaine :**
- `RssItem` — article RSS avec userId, statut d'analyse (`AnalysisRequestStatus`) et contenu d'analyse. Méthodes de transition : `requestAnalysis()`, `startAnalysis()`, `completeAnalysis(String)`, `failAnalysis()`.
- `RssFeedSource` — source RSS avec userId. L'id est dérivé de `UUID.nameUUIDFromBytes((userId + url).getBytes())` pour garantir l'unicité par utilisateur.
- `AnalysisRequestStatus` — enum : `NOT_REQUESTED`, `PENDING`, `IN_PROGRESS`, `COMPLETED`, `FAILED`

**Services applicatifs :**
- `LoadArticlesUseCaseService` — `@Scheduled` toutes les 60 min. Charge les articles de toutes les sources (`getAllSources()`), déduplique, sauvegarde.
- `AnalyzeArticleUseCaseService` — `@Scheduled` toutes les 10 sec. Dépile les articles `PENDING`, appelle Mistral, met à jour le statut.
- `RequestArticleAnalysisUseCaseService` — Passe l'article en `PENDING`. Si déjà `IN_PROGRESS`, ne change pas le statut.
- `GetSourcesUseCaseService` — Retourne les sources de l'utilisateur courant.
- `AddSourceUseCaseService` — Valide l'URL, crée la source avec userId.

**Endpoints REST :**
| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/articles` | Articles de l'utilisateur courant |
| `GET` | `/api/article/{id}/analyze` | Demande d'analyse d'un article |
| `GET` | `/api/sources` | Sources RSS de l'utilisateur courant |
| `POST` | `/api/sources` | Ajoute une source RSS (`url` obligatoire, `name` obligatoire, `description` optionnel) |

**Lancer le backend :**
```bash
# Dev (auth par header X-User-Id)
cd srv && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production (Firebase Auth)
cd srv && ./mvnw spring-boot:run
```

**Tests :**
```bash
cd srv && ./mvnw test
```

## Frontend (`front/`)

**Stack :** Vue 3, TypeScript, Vite (rolldown), Naive UI

**Architecture hexagonale (miroir du backend) :**
- `domain/` — modèles (`Article`, `RequestStatus`)
- `ports/` — interfaces (`ArticleRepository`, `RequestAnalysis`)
- `application/` — use cases (`GetArticles`, `RequestAnalysis`)
- `adapters/` — implémentation HTTP (`NewsfeedArticles` → `http://localhost:8080`)
- `components/` — `App`, `Header`, `ArticleList`, `ArticleItem`, `Background`

**Lancer le frontend :**
```bash
cd front && npm install && npm run dev   # http://localhost:5173
```

**Build :**
```bash
cd front && npm run build
```

## Authentification

Deux modes selon le profil Spring :

- **`dev`** (`@Profile("dev")`) — `DevAuthenticationFilter` lit le header `X-User-Id`. Pas de Firebase.
  ```
  X-User-Id: mon-user-id
  ```
- **`!dev`** (production) — `FirebaseAuthenticationFilter` valide le token Bearer Firebase. `FirebaseConfig` charge `firebase.json` depuis le classpath.

Routes `/app/**` sont publiques, tout le reste (`/api/*`) requiert une authentification.

## Fonctionnement global

1. `LoadArticlesUseCaseService` charge les articles toutes les 60 minutes depuis toutes les sources RSS (tous utilisateurs)
2. Les articles sont dédupliqués par `(userId + url)` et sauvegardés en MongoDB
3. L'utilisateur peut demander une analyse via `GET /api/article/{id}/analyze` → statut passe à `PENDING`
4. `AnalyzeArticleUseCaseService` dépile les articles `PENDING` toutes les 10 secondes, appelle Mistral, stocke l'analyse structurée
5. Le frontend affiche les articles avec leur statut d'analyse et permet de déclencher une nouvelle analyse

## Variables d'environnement

Le fichier `.env` (non versionné) contient les secrets nécessaires (clé Mistral, config Firebase, URI MongoDB).

Configuration dans `application.properties` :
- `spring.data.mongodb.uri` — URI MongoDB (depuis env)
- `spring.ai.mistralai.api-key` — Clé API Mistral
- `spring.ai.mistralai.chat.options.model=mistral-large-latest`

En dev, `application-dev.properties` surcharge l'URI MongoDB avec `mongodb://localhost:27017`.
