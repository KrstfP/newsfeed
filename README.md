# Newsfeed

Agrégateur de flux RSS avec analyse IA géopolitique/militaire.

[![CI/CD](https://github.com/krstfp/newsfeed/actions/workflows/ci.yml/badge.svg)](https://github.com/krstfp/newsfeed/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=KrstfP_newsfeed&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=KrstfP_newsfeed)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=KrstfP_newsfeed&metric=coverage)](https://sonarcloud.io/summary/new_code?id=KrstfP_newsfeed)

## Stack

- **Backend** : Spring Boot 3.5, Java 21, MongoDB, Spring AI (Mistral), Firebase Auth
- **Frontend** : Vue 3, TypeScript, Vite, Naive UI

## Lancement rapide

```bash
# Backend (mode dev — auth par header X-User-Id)
cd srv && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
cd front && npm install && npm run dev
```

Voir [CLAUDE.md](CLAUDE.md) pour la documentation complète de l'architecture.
