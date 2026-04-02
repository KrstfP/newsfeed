<template>
  <n-layout-header bordered class="app-header">
    <div class="header-content">
      <div class="logo">
        <Title />
      </div>

      <div class="right">
        <!-- futur bouton / dark mode / login -->
      </div>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { NLayoutHeader } from "naive-ui"
import Title from './Title.vue';
</script>

<style scoped>
.app-header {
  height: 56px;
  padding: 0;
  display: flex;
  align-items: center;
  background: transparent; /* clean */
}

.header-content {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
    height: 100%;
  width: 100%;
  display: flex;
  align-items: left;
}
</style>