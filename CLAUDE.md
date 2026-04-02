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
| `GET` | `/api/articles` | Articles de l'utilisateur courant — retourne `analysis` (Markdown, nullable) |
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

**Stack :** Vue 3, TypeScript, Vite (rolldown), Naive UI, marked (rendu Markdown)

**Architecture hexagonale (miroir du backend) :**
- `domain/` — modèles (`Article`, `RequestStatus`) — `Article` inclut `analysis: string | null`
- `ports/` — interfaces (`ArticleRepository`, `RequestAnalysis`)
- `application/` — use cases (`GetArticles`, `RequestAnalysis`)
- `adapters/` — implémentation HTTP (`NewsfeedArticles`, `NewsfeedSources`) — URLs relatives `/api/*`
- `components/` — `App`, `ArticleList`, `ArticleItem`, `AnalysisModal`, `Background`, `Sidebar`, etc.

**Lancer le frontend :**
```bash
cd front && npm install && npm run dev   # http://localhost:5173
```

Le serveur de dev Vite proxifie `/api/*` vers `http://localhost:8080` (configuré dans `vite.config.ts`).

**Build :**
```bash
cd front && npm run build   # output dans front/dist/
```

## Authentification

Deux modes selon le profil Spring :

- **`dev`** (`@Profile("dev")`) — `DevAuthenticationFilter` lit le header `X-User-Id`. Pas de Firebase.
  ```
  X-User-Id: mon-user-id
  ```
- **`!dev`** (production) — `FirebaseAuthenticationFilter` valide le token Bearer Firebase. `FirebaseConfig` charge les credentials depuis `FIREBASE_CREDENTIALS_PATH` (env var) ou `firebase.json` en classpath.

Routes `/app/**` sont publiques, tout le reste (`/api/*`) requiert une authentification.

## Infrastructure & Déploiement

### Docker

2 images Docker séparées :

**`Dockerfile.backend`** — multi-stage Maven + JRE 21 Alpine
- JVM tunée pour Render free tier : `-Xmx200m -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m`

**`Dockerfile.frontend`** — multi-stage Node 22 + nginx Alpine
- Build Vite → fichiers statiques servis par nginx
- nginx proxifie `/api/*` vers le backend via `BACKEND_URL` et `BACKEND_HOST`
- `Origin` header strippé avant forwarding (évite les faux positifs CORS côté backend)
- Template nginx : `front/nginx.conf.template` (processsé par `envsubst` au démarrage)

**`docker-compose.yml`** — dev local avec les 2 services :
```bash
docker-compose up
# Frontend sur http://localhost:80
# Backend sur http://localhost:8080
```

### CI/CD — GitHub Actions (`.github/workflows/ci.yml`)

- **Sur PR** : job `test` uniquement (`cd srv && ./mvnw test`)
- **Sur push `main`** : `test` → `build-and-push` → `deploy`
  - Images poussées sur GHCR : `ghcr.io/krstfp/newsfeed/backend:latest` et `.../frontend:latest`
  - Auth GHCR via `GITHUB_TOKEN` (pas de secret supplémentaire)
  - Déploiement Render via deploy hooks

**GitHub Secrets requis :**
| Secret | Usage |
|--------|-------|
| `VITE_FIREBASE_API_KEY` | Baked dans le bundle Vite au build |
| `VITE_FIREBASE_AUTH_DOMAIN` | Baked dans le bundle Vite au build |
| `VITE_FIREBASE_PROJECT_ID` | Baked dans le bundle Vite au build |
| `RENDER_BACKEND_DEPLOY_HOOK_URL` | Deploy hook Render backend |
| `RENDER_FRONTEND_DEPLOY_HOOK_URL` | Deploy hook Render frontend |

### Hébergement — Render (free tier)

**Service backend** :
- Image : `ghcr.io/krstfp/newsfeed/backend:latest`
- Env vars : `MISTRAL_API_KEY`, `MONGODB_URI`, `FIREBASE_CREDENTIALS_PATH=/etc/secrets/firebase.json`
- Secret File : `firebase.json` monté à `/etc/secrets/firebase.json`

**Service frontend** :
- Image : `ghcr.io/krstfp/newsfeed/frontend:latest`
- Env vars : `BACKEND_URL=https://<backend>.onrender.com`, `BACKEND_HOST=<backend>.onrender.com`

## Variables d'environnement

Le fichier `.env` (non versionné) contient les secrets nécessaires (clé Mistral, config Firebase, URI MongoDB).

Configuration dans `application.properties` :
- `spring.data.mongodb.uri` — URI MongoDB Atlas
- `spring.ai.mistralai.api-key` — Clé API Mistral
- `spring.ai.mistralai.chat.options.model=mistral-large-latest`

En dev, `application-dev.properties` surcharge l'URI MongoDB avec `mongodb://localhost:27017`.
