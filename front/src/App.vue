<script setup lang="ts">
import ArticleList from './components/ArticleList.vue'
import Background from './components/Background.vue';
import Header from './components/Header.vue';
</script>

<template>
  <Header />
  <Background />
  <ArticleList />
</template>

<style scoped>
* {
  background-color: #fafafa;
}
</style>