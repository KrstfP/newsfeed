<script setup lang="ts">
import type { Article } from "../domain/Article";
import { NText, NTag, NButton, NIcon, NEllipsis, NSpace } from "naive-ui";
import {
  BrainCircuit20Filled,
  Clock20Regular,
  CheckmarkCircle20Regular,
  DismissCircle20Regular,
} from "@vicons/fluent";

defineProps<{
  article: Article;
}>();

const emit = defineEmits<{
  (e: "analyze", article: Article): void;
}>();

// Mapping catégories → type Naive UI (theme-safe)
const tagColors: Record<
  string,
  "default" | "primary" | "success" | "info" | "warning" | "error"
> = {
  Tech: "primary",
  Business: "success",
  AI: "info",
  Security: "error",
};

const statusMap = {
  NOT_REQUESTED: {
    icon: BrainCircuit20Filled,
    type: "info",
    loading: false,
  },
  PENDING: {
    icon: Clock20Regular,
    type: "warning",
    loading: false,
  },
  IN_PROGRESS: {
    icon: Clock20Regular,
    type: "info",
    loading: true,
  },
  COMPLETED: {
    icon: CheckmarkCircle20Regular,
    type: "success",
    loading: false,
  },
  FAILED: {
    icon: DismissCircle20Regular,
    type: "error",
    loading: false,
  },
} as const;
</script>

<template>
  <div class="article-container">
    <div class="article-item">
      <div class="cell source">
        <n-ellipsis :tooltip="true">
          <a
            :href="article.url"
            target="_blank"
            rel="noopener noreferrer"
            class="source-link"
          >
            {{ article.source }}
          </a>
        </n-ellipsis>
      </div>

      <!-- Titre -->
      <div class="cell content">
        <n-button
          size="tiny"
          tertiary
          :type="statusMap[article.analysisRequestStatus]?.type || 'info'"
          :loading="statusMap[article.analysisRequestStatus]?.loading"
          :disabled="statusMap[article.analysisRequestStatus]?.loading"
          circle
          @click="emit('analyze', article)"
          class="analyze-btn"
        >
          <template #icon>
            <n-icon>
              <component :is="statusMap[article.analysisRequestStatus]?.icon" />
            </n-icon>
          </template>
        </n-button>
        <n-button
          size="tiny"
          circle
          tertiary
          :type="statusMap[article.analysisRequestStatus]?.type || 'info'"
          :loading="statusMap[article.analysisRequestStatus]?.loading"
          :disabled="statusMap[article.analysisRequestStatus]?.loading"
          class="analyze-btn"
        >
          <template #icon>
            <n-icon>
              <Clock20Regular />
            </n-icon>
          </template>
        </n-button>
        <n-text depth="3">
          <n-ellipsis :line-clamp="1" expand-trigger="click" :tooltip="false">
            <span class="title-text">{{ article.title }}</span>
            <span class="description"> • {{ article.description }}</span>
            <n-space size="small" wrap class="tags">
              <n-tag
                v-for="category in article.categories"
                :key="category"
                :bordered="false"
                :type="tagColors[category] || 'default'"
                size="small"
              >
                {{ category }}
              </n-tag>
            </n-space>
          </n-ellipsis>
        </n-text>
      </div>

      <!-- Date -->
      <div class="cell date">
        <n-text depth="3">
          {{ new Date(article.publishedAt).toLocaleDateString() }}
        </n-text>
      </div>
    </div>
  </div>
</template>

<style scoped>
.article-container {
  container-type: inline-size;
}

.article-item {
  padding: 2px 0;
  display: grid;
  grid-template-columns: 100px 1fr 70px; /* source | date | actions | content */
  align-items: start;
  gap: 5px;
}

/* Ligne 1 : bouton + titre + source inline */
.title-line {
  display: block; /* flow normal */
}

.analyze-btn {
  vertical-align: middle;
  margin-right: 6px;
}

.title-text {
  display: inline; /* texte continue avec source */
  vertical-align: middle;
  font-weight: bolder;
  font-size: small;
}

.source {
  color: inherit; /* theme-safe */
  text-decoration: none;
  font-weight: lighter;
}

.date {
  font-weight: lighter;
  font-size: xx-small;
}

/* Ligne 2 : description */
.description {
  text-align: left;
  vertical-align: middle;
  font-size: small;
}

.cell {
  font-size: smaller;
  font-weight: light;
  overflow: hidden;
  min-width: 0; /* ⚠️ TRÈS IMPORTANT avec grid */
  vertical-align: top;
}

.source-link {
  text-decoration: none;
  color: inherit;
}

.cell.content {
  display: flex;
  align-items: center;
  min-width: 0; /* obligatoire pour ellipsis en flex */
  align-items: start;
}

.title-wrapper {
  flex: 1;
  min-width: 0; /* très important */
}

@container (max-width: 700px) {
  .cell.date {
    display: none;
  }

  .article-item {
    grid-template-columns: 80px 1fr; /* date | actions + content */
  }
}

@container (max-width: 500px) {
  .cell.source {
    display: none;
  }

  .article-item {
    grid-template-columns: 1fr; /* actions + content */
  }
}
.tags {
  padding-top: 5px;
  padding-bottom: 5px;
}
</style>
