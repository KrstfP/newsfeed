<script setup lang="ts">
import type { Article } from "../domain/Article";
import { NText, NTag, NButton, NIcon, NEllipsis, NSpace } from "naive-ui";
import { BrainCircuit20Filled, Clock20Regular, CheckmarkCircle20Regular, DismissCircle20Regular } from "@vicons/fluent";

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

const statusMap =  {
  NOT_REQUESTED: {
    icon: BrainCircuit20Filled,
    type: 'info',
    loading: false
  },
  PENDING: {
    icon: Clock20Regular,
    type: 'warning',
    loading: false
  },
  IN_PROGRESS: {
    icon: Clock20Regular,
    type: 'info',
    loading: true
  },
  COMPLETED: {
    icon: CheckmarkCircle20Regular,
    type: 'success',
    loading: false
  },
  FAILED: {
    icon: DismissCircle20Regular,
    type: 'error',
    loading: false
  }
} as const

</script>

<template>
  <div class="article-item">
    <!-- Ligne 1 : bouton Analyze + titre + source -->
    <div class="title-line">
      <!-- Bouton Analyze -->
      <n-button
        size="tiny"
        tertiary
        :type="statusMap[article.analysisRequestStatus]?.type ||'info'"
        :loading="statusMap[article.analysisRequestStatus]?.loading"
        :disabled="statusMap[article.analysisRequestStatus]?.loading"
        circle
        @click="emit('analyze', article)"
        class="analyze-btn"
      >
        <template
            #icon>
          <n-icon>
            <component :is="statusMap[article.analysisRequestStatus]?.icon" />
          </n-icon>
        </template>
      </n-button>

      <!-- Titre + source inline -->
      <span class="title-text">
        {{ article.title }}
      </span>
      <n-text
          depth="3"
          tag="a"
          class="source"
          :href="article.url"
          target="_blank"
          rel="noopener noreferrer"
        >
          ({{ article.source }})
        </n-text>
    </div>

    <!-- Ligne 2 : description -->
    <n-text depth="3" class="description">
      <n-ellipsis :line-clamp="1" expand-trigger="click" :tooltip="false">
        {{ new Date(article.publishedAt).toLocaleDateString() }} • {{ article.description }}
      </n-ellipsis>
    </n-text>

    <!-- Ligne 3 : catégories -->
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
  </div>
</template>

<style scoped>
.article-item {
  padding: 10px 0;
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
  font-weight: 600; /* titre gras */
}

.source {
  font-size: 12px;
  color: inherit; /* theme-safe */
  margin-left: 4px;
  text-decoration: none;
}

/* Ligne 2 : description */
.description {
  text-align: left;
}

/* Ligne 3 : tags */
.tags {

}
</style>