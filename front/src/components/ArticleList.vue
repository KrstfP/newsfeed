<script setup lang="ts">
import { ref, inject, onMounted, watch } from 'vue'
import type { Article, ArticleFilters } from '../domain/Article'
import type { AuthService } from '../ports/AuthService'
import ArticleItem from './ArticleItem.vue'
import AnalysisModal from './AnalysisModal.vue'
import { getArticles } from '../application/GetArticles'
import { NewsfeedArticleRepository } from '../adapters/NewsfeedArticles'

const authService = inject<AuthService>('authService')!
const articles = ref<Article[]>([])
const repo = new NewsfeedArticleRepository(authService)

const analyzedFilter = ref<boolean | undefined>(undefined)
const sinceFilter = ref<string | undefined>(undefined)

function yesterday(): string {
  const d = new Date()
  d.setDate(d.getDate() - 1)
  return d.toISOString().slice(0, 10)
}

function toggleAnalyzed() {
  analyzedFilter.value = analyzedFilter.value === true ? undefined : true
}

function toggleSince() {
  sinceFilter.value = sinceFilter.value ? undefined : yesterday()
}

async function reload() {
  const filters: ArticleFilters = {}
  if (analyzedFilter.value !== undefined) filters.analyzed = analyzedFilter.value
  if (sinceFilter.value) filters.since = sinceFilter.value
  articles.value = await getArticles(repo, filters)
}

onMounted(reload)
watch([analyzedFilter, sinceFilter], reload)

function onAnalyze(article: Article) {
  repo.requestAnalysis(article)
}

const analysisTarget = ref<Article | null>(null)

function onOpenAnalysis(article: Article) {
  analysisTarget.value = article
}
</script>

<template>
  <div class="panel">
    <div class="topbar">
      <span class="panel-title">Today</span>
      <span class="article-count">{{ articles.length }} articles</span>
    </div>

    <div class="filters">
      <button class="chip" :class="{ active: analyzedFilter === true }" @click="toggleAnalyzed">
        Analysés
      </button>
      <button class="chip" :class="{ active: !!sinceFilter }" @click="toggleSince">
        &lt; 24h
      </button>
    </div>

    <div class="col-headers">
      <span class="col-source">SOURCE</span>
      <span class="col-content">ARTICLE</span>
      <span class="col-date">DATE</span>
    </div>
    <div class="col-sep" />

    <div class="list">
      <ArticleItem
        v-for="article in articles"
        :key="article.id"
        :article="article"
        @analyze="onAnalyze"
        @open-analysis="onOpenAnalysis"
      />
    </div>
  </div>

  <AnalysisModal
    :article="analysisTarget"
    :show="analysisTarget !== null"
    @close="analysisTarget = null"
  />
</template>

<style scoped>
.panel {
  min-height: 100%;
}

.topbar {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding: 16px 32px 14px;
  border-bottom: 1px solid #e0e0e0;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e1e1e;
}

.article-count {
  font-size: 11px;
  color: #aaa;
}

.col-headers {
  display: grid;
  grid-template-columns: 108px 1fr 65px;
  gap: 8px;
  padding: 8px 32px 6px;
  font-size: 9px;
  font-weight: 500;
  letter-spacing: 0.08em;
  color: #aaa;
}

/* offset col-content to align with article title (after the 26px icon) */
.col-content {
  padding-left: 34px;
}

.filters {
  display: flex;
  gap: 6px;
  padding: 8px 32px 4px;
}

.chip {
  padding: 3px 10px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  background: #fff;
  font-size: 11px;
  color: #666;
  cursor: pointer;
  transition: background 0.12s, color 0.12s, border-color 0.12s;
  white-space: nowrap;
}

.chip:hover {
  background: #f0f0f0;
}

.chip.active {
  background: #e8eeff;
  border-color: #c5d0fb;
  color: #2a6ef5;
  font-weight: 500;
}

@media (max-width: 768px) {
  .col-headers {
    grid-template-columns: 1fr 55px;
  }

  .col-source {
    display: none;
  }

  .col-content {
    padding-left: 38px; /* aligne avec le contenu (26px icône + 8px gap) */
  }

  .list {
    padding: 0 16px;
  }

  .filters {
    padding: 8px 16px 4px;
  }
}

.col-sep {
  height: 1px;
  background: #e0e0e0;
  margin: 0 32px;
}

.list {
  padding: 0 32px;
}
</style>
