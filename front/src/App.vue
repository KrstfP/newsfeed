<script setup lang="ts">
import { ref, provide, computed } from 'vue'
import { onAuthStateChanged, signInWithPopup, GoogleAuthProvider, signOut as fbSignOut } from 'firebase/auth'
import type { User } from 'firebase/auth'
import { auth } from './adapters/firebase'
import { DevAuthService } from './adapters/DevAuthService'
import { FirebaseAuthService } from './adapters/FirebaseAuthService'
import type { AuthService } from './ports/AuthService'
import Background from './components/Background.vue'
import Sidebar from './components/Sidebar.vue'
import ArticleList from './components/ArticleList.vue'
import SourceList from './components/SourceList.vue'
import LoginView from './components/LoginView.vue'

const isDev = import.meta.env.VITE_AUTH_MODE === 'dev'
const mobileMenuOpen = ref(false)

// Injection de l'implémentation correcte selon le mode
const authService: AuthService = isDev ? new DevAuthService() : new FirebaseAuthService()
provide('authService', authService)

// État d'authentification
const user = ref<User | null>(null)
const authLoading = ref(!isDev)
const view = ref<'today' | 'sources'>('today')

if (!isDev) {
  onAuthStateChanged(auth, (u) => {
    user.value = u
    authLoading.value = false
  })
}

const isAuthenticated = computed(() => isDev || !!user.value)

// Nom affiché : displayName Firebase ou userId dev
const displayName = computed(() =>
  isDev
    ? (import.meta.env.VITE_DEV_USER_ID ?? 'dev-user')
    : (user.value?.displayName ?? user.value?.email ?? 'Utilisateur')
)
const photoUrl = computed(() => user.value?.photoURL ?? undefined)

async function signIn() {
  await signInWithPopup(auth, new GoogleAuthProvider())
}

async function signOut() {
  await fbSignOut(auth)
}
</script>

<template>
  <!-- Chargement initial (vérification de session Firebase) -->
  <div v-if="authLoading" class="splash">
    <span class="splash-logo">newsfeed</span>
  </div>

  <!-- Page de connexion -->
  <LoginView v-else-if="!isAuthenticated" @sign-in="signIn" />

  <!-- Application -->
  <template v-else>
    <Background />
    <div class="app-layout">
      <!-- Topbar mobile -->
      <div class="mobile-topbar">
        <span class="mobile-logo">newsfeed</span>
        <button class="hamburger" @click="mobileMenuOpen = !mobileMenuOpen">☰</button>
      </div>

      <!-- Backdrop fermeture sidebar -->
      <div v-if="mobileMenuOpen" class="sidebar-backdrop" @click="mobileMenuOpen = false" />

      <Sidebar
        :class="{ open: mobileMenuOpen }"
        :current-view="view"
        :user-name="displayName"
        :user-photo="photoUrl"
        @navigate="v => { view = v; mobileMenuOpen = false }"
        @sign-out="signOut"
      />
      <main class="main">
        <ArticleList v-if="view === 'today'" />
        <SourceList v-else />
      </main>
    </div>
  </template>
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

/* Écran de chargement pendant la vérification de session */
.splash {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.splash-logo {
  font-size: 18px;
  font-weight: 600;
  color: #ccc;
  letter-spacing: 0.01em;
}

/* Mobile topbar — masqué sur desktop */
.mobile-topbar {
  display: none;
}

.sidebar-backdrop {
  display: none;
}

@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
  }

  .mobile-topbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 48px;
    padding: 0 16px;
    background: #f4f4f4;
    border-bottom: 1px solid #e0e0e0;
    flex-shrink: 0;
    z-index: 10;
  }

  .mobile-logo {
    font-size: 14px;
    font-weight: 600;
    color: #1e1e1e;
    letter-spacing: 0.01em;
  }

  .hamburger {
    background: none;
    border: none;
    font-size: 18px;
    cursor: pointer;
    color: #555;
    padding: 4px 8px;
    border-radius: 4px;
    line-height: 1;
    transition: background 0.12s;
  }

  .hamburger:hover {
    background: #e8e8e8;
  }

  .sidebar-backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 199;
  }
}
</style>
