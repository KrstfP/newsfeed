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
      <Sidebar
        :current-view="view"
        :user-name="displayName"
        :user-photo="photoUrl"
        @navigate="v => view = v"
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
</style>
