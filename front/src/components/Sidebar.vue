<script setup lang="ts">
defineProps<{ currentView: 'today' | 'sources' }>()
defineEmits<{ (e: 'navigate', view: 'today' | 'sources'): void }>()
</script>

<template>
  <aside class="sidebar">
    <div class="logo">newsfeed</div>
    <div class="sep" />

    <nav class="nav">
      <button
        class="nav-item"
        :class="{ active: currentView === 'today' }"
        @click="$emit('navigate', 'today')"
      >
        <span class="nav-icon">◈</span>
        Today
      </button>
      <button
        class="nav-item"
        :class="{ active: currentView === 'sources' }"
        @click="$emit('navigate', 'sources')"
      >
        <span class="nav-icon">⊞</span>
        Sources
      </button>
    </nav>

    <div class="spacer" />
    <div class="sep" />
    <button class="nav-item settings">Settings</button>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 200px;
  flex-shrink: 0;
  height: 100vh;
  background: #f4f4f4;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  padding: 0 0 12px 0;
}

.logo {
  padding: 20px 20px 18px;
  font-size: 14px;
  font-weight: 600;
  color: #1e1e1e;
  letter-spacing: 0.01em;
}

.sep {
  height: 1px;
  background: #e0e0e0;
  margin: 0 20px;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 10px 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 7px 10px;
  background: none;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  text-align: left;
  transition: background 0.12s, color 0.12s;
}

.nav-item:hover {
  background: #e8e8e8;
}

.nav-item.active {
  background: #e8eeff;
  color: #2a6ef5;
  font-weight: 500;
}

.nav-icon {
  font-size: 15px;
  line-height: 1;
}

.spacer {
  flex: 1;
}

.settings {
  margin: 0 12px;
  color: #999;
  font-size: 12px;
}

.settings:hover {
  background: #e8e8e8;
  color: #555;
}
</style>
