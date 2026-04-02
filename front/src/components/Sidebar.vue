<script setup lang="ts">
defineProps<{
  currentView: 'today' | 'sources'
  userName?: string
  userPhoto?: string
}>()

defineEmits<{
  (e: 'navigate', view: 'today' | 'sources'): void
  (e: 'signOut'): void
}>()
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

    <div class="user-section">
      <div class="sep" style="margin-bottom: 12px" />
      <div class="user-row">
        <img v-if="userPhoto" :src="userPhoto" class="user-avatar" referrerpolicy="no-referrer" />
        <div v-else class="user-avatar placeholder">{{ userName?.[0]?.toUpperCase() ?? '?' }}</div>
        <span class="user-name">{{ userName ?? 'Utilisateur' }}</span>
        <button class="btn-signout" title="Se déconnecter" @click="$emit('signOut')">⎋</button>
      </div>
    </div>
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
  padding: 0 0 16px 0;
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

/* User section */
.user-section {
  padding: 0 12px 0;
}

.user-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  flex-shrink: 0;
  object-fit: cover;
}

.user-avatar.placeholder {
  background: #2a6ef5;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 12px;
  color: #555;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-signout {
  background: none;
  border: none;
  color: #aaa;
  cursor: pointer;
  font-size: 14px;
  padding: 0 2px;
  flex-shrink: 0;
  transition: color 0.12s;
}

.btn-signout:hover {
  color: #555;
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 200;
    width: 240px;
    transform: translateX(-100%);
    transition: transform 0.22s ease;
    box-shadow: 2px 0 16px rgba(0, 0, 0, 0.1);
  }

  .sidebar.open {
    transform: translateX(0);
  }
}
</style>
