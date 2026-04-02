<script setup lang="ts">
import { ref } from 'vue'
import Background from './components/Background.vue'
import Sidebar from './components/Sidebar.vue'
import ArticleList from './components/ArticleList.vue'
import SourceList from './components/SourceList.vue'

const view = ref<'today' | 'sources'>('today')
</script>

<template>
  <Background />
  <div class="app-layout">
    <Sidebar :current-view="view" @navigate="v => view = v" />
    <main class="main">
      <ArticleList v-if="view === 'today'" />
      <SourceList v-else />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main {
  flex: 1;
  overflow-y: auto;
  background: #fafafa;
  min-width: 0;
}
</style>
