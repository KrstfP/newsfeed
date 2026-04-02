<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{ (e: 'signIn'): void }>()

const loading = ref(false)
const error = ref('')

async function handleSignIn() {
  loading.value = true
  error.value = ''
  try {
    await emit('signIn')
  } catch {
    error.value = 'Connexion impossible. Réessaie.'
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="card">
      <div class="logo">newsfeed</div>
      <p class="subtitle">Agrégateur RSS avec analyse géopolitique</p>

      <button class="btn-google" :disabled="loading" @click="handleSignIn">
        <svg class="google-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
          <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
          <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
          <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
        </svg>
        {{ loading ? 'Connexion…' : 'Continuer avec Google' }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 48px 40px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  width: 340px;
}

.logo {
  font-size: 22px;
  font-weight: 600;
  color: #1e1e1e;
  letter-spacing: 0.01em;
}

.subtitle {
  font-size: 13px;
  color: #888;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 8px;
}

.btn-google {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: background 0.12s, border-color 0.12s;
  justify-content: center;
}

.btn-google:hover:not(:disabled) {
  background: #f5f5f5;
  border-color: #bbb;
}

.btn-google:disabled {
  opacity: 0.6;
  cursor: default;
}

.google-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.error {
  font-size: 12px;
  color: #c42b2b;
  text-align: center;
}
</style>
