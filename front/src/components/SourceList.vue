<script setup lang="ts">
import { ref, inject, onMounted } from 'vue'
import type { Source } from '../domain/Source'
import type { AuthService } from '../ports/AuthService'
import { getSources } from '../application/GetSources'
import { NewsfeedSourceRepository } from '../adapters/NewsfeedSources'

const authService = inject<AuthService>('authService')!
const sources = ref<Source[]>([])
const repo = new NewsfeedSourceRepository(authService)

const newUrl = ref('')
const newName = ref('')
const newDescription = ref('')
const adding = ref(false)
const error = ref('')
const deletingId = ref<string | null>(null)

onMounted(async () => {
  sources.value = await getSources(repo)
})

async function addSource() {
  error.value = ''
  if (!newUrl.value.trim() || !newName.value.trim()) {
    error.value = 'URL et nom sont requis.'
    return
  }
  adding.value = true
  try {
    await repo.add(newUrl.value.trim(), newName.value.trim(), newDescription.value.trim() || undefined)
    sources.value = await getSources(repo)
    newUrl.value = ''
    newName.value = ''
    newDescription.value = ''
  } catch {
    error.value = "Erreur lors de l'ajout de la source."
  } finally {
    adding.value = false
  }
}

async function deleteSource(source: Source) {
  deletingId.value = source.id
  error.value = ''
  try {
    await repo.delete(source.id)
    sources.value = sources.value.filter(s => s.id !== source.id)
  } catch {
    error.value = `Impossible de supprimer "${source.name}".`
  } finally {
    deletingId.value = null
  }
}
</script>

<template>
  <div class="panel">
    <div class="topbar">
      <span class="panel-title">Sources</span>
      <span class="article-count">{{ sources.length }} sources</span>
    </div>

    <div class="source-list">
      <div v-for="source in sources" :key="source.id" class="source-row">
        <div class="source-body">
          <div class="source-info">
            <a :href="source.url" target="_blank" rel="noopener noreferrer" class="source-name">
              {{ source.name }}
            </a>
            <span v-if="source.description" class="source-desc"> · {{ source.description }}</span>
          </div>
          <span class="source-url">{{ source.url }}</span>
        </div>
        <button
          class="btn-delete"
          :disabled="deletingId === source.id"
          title="Supprimer cette source"
          @click="deleteSource(source)"
        >
          <svg viewBox="0 0 20 20" fill="currentColor" class="icon-trash">
            <path fill-rule="evenodd" d="M8.75 1A2.75 2.75 0 006 3.75v.443c-.795.077-1.58.176-2.365.298a.75.75 0 10.23 1.482l.149-.022.841 10.518A2.75 2.75 0 007.596 19h4.807a2.75 2.75 0 002.742-2.53l.841-10.52.149.023a.75.75 0 00.23-1.482A41.03 41.03 0 0014 4.193v-.443A2.75 2.75 0 0011.25 1h-2.5zm0 1.5h2.5c.69 0 1.25.56 1.25 1.25v.345a43.4 43.4 0 00-5 0v-.345c0-.69.56-1.25 1.25-1.25zM6.05 6.015l.096-.007A41.9 41.9 0 0110 5.75c1.305 0 2.594.087 3.855.258l.095.007-.826 10.326a1.25 1.25 0 01-1.247 1.159H7.596a1.25 1.25 0 01-1.247-1.159L5.523 6.015h.527z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <div v-if="sources.length === 0" class="empty">
        Aucune source configurée.
      </div>
    </div>

    <div v-if="error" class="error-banner">{{ error }}</div>

    <div class="add-section">
      <div class="add-title">Ajouter une source</div>
      <div class="form">
        <input v-model="newName" placeholder="Nom *" class="input" />
        <input v-model="newUrl" placeholder="URL du flux RSS *" class="input url-input" />
        <input v-model="newDescription" placeholder="Description (optionnel)" class="input" />
        <button class="btn-add" :disabled="adding" @click="addSource">
          {{ adding ? 'Ajout…' : '+ Ajouter' }}
        </button>
      </div>
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

.source-list {
  padding: 8px 32px;
}

.source-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #e8e8e8;
}

.source-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.source-info {
  font-size: 13px;
}

.source-name {
  font-weight: 500;
  color: #1e1e1e;
  text-decoration: none;
}

.source-name:hover {
  text-decoration: underline;
}

.source-desc {
  color: #888;
  font-size: 12px;
}

.source-url {
  font-size: 11px;
  color: #bbb;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-delete {
  flex-shrink: 0;
  background: none;
  border: none;
  padding: 5px;
  border-radius: 5px;
  cursor: pointer;
  color: #ccc;
  display: flex;
  align-items: center;
  transition: color 0.12s, background 0.12s;
}

.btn-delete:hover:not(:disabled) {
  color: #c42b2b;
  background: #fff0f0;
}

.btn-delete:disabled {
  opacity: 0.4;
  cursor: default;
}

.icon-trash {
  width: 16px;
  height: 16px;
}

.empty {
  padding: 24px 0;
  color: #aaa;
  font-size: 13px;
}

.error-banner {
  margin: 0 32px;
  padding: 8px 12px;
  background: #fff0f0;
  border: 1px solid #f5c2c2;
  border-radius: 6px;
  font-size: 12px;
  color: #c42b2b;
}

.add-section {
  padding: 24px 32px;
  border-top: 1px solid #e0e0e0;
  margin-top: 8px;
}

.add-title {
  font-size: 12px;
  font-weight: 600;
  color: #555;
  letter-spacing: 0.05em;
  margin-bottom: 12px;
  text-transform: uppercase;
}

.form {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.input {
  padding: 7px 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 13px;
  background: #fff;
  color: #1e1e1e;
  outline: none;
  transition: border-color 0.15s;
}

.input:focus {
  border-color: #2a6ef5;
}

.url-input {
  min-width: 280px;
}

.btn-add {
  padding: 7px 16px;
  background: #2a6ef5;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}

.btn-add:hover:not(:disabled) {
  background: #1a5ee0;
}

.btn-add:disabled {
  opacity: 0.6;
  cursor: default;
}
</style>
