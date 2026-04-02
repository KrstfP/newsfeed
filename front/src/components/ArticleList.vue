<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Article } from '../domain/Article'
import ArticleItem from './ArticleItem.vue'
import { getArticles } from '../application/GetArticles'
import { NewsfeedArticleRepository } from '../adapters/NewsfeedArticles'

const articles = ref<Article[]>([])
const repo = new NewsfeedArticleRepository()

onMounted(async () => {
  articles.value = await getArticles(repo)
})

function onAnalyze(article: Article) {
  repo.requestAnalysis(article)
}
</script>

<template>
  <div class="panel">
    <div class="topbar">
      <span class="panel-title">Today</span>
      <span class="article-count">{{ articles.length }} articles</span>
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
      />
    </div>
  </div>
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

.col-sep {
  height: 1px;
  background: #e0e0e0;
  margin: 0 32px;
}

.list {
  padding: 0 32px;
}
</style>
