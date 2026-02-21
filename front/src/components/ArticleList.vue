<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { Article } from "../domain/Article";
// Import des composants Naive UI que tu utilises
import { NList } from "naive-ui";
import ArticleItem from "./ArticleItem.vue";
import { getArticles } from "../application/GetArticles";
import { NewsfeedArticleRepository } from "../adapters/NewsfeedArticles";

const articles = ref<Article[]>([]);
const repo = new NewsfeedArticleRepository();

onMounted(async () => {
  articles.value = await getArticles(repo);
});

function onAnalyze(article: Article) {
  repo.requestAnalysis(article);
}
</script>

<template>
  <div>
    <n-list>
      <ArticleItem
        v-for="article in articles"
        :key="article.id"
        :article="article"
        @analyze="onAnalyze"
      />
    </n-list>
  </div>
</template>

<style scoped>
* {
  background: none;
}
</style>