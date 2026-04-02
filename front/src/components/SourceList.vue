<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Source } from '../domain/Source'
import { getSources } from '../application/GetSources'
import { NewsfeedSourceRepository } from '../adapters/NewsfeedSources'

const sources = ref<Source[]>([])
const repo = new NewsfeedSourceRepository()

const newUrl = ref('')
const newName = ref('')
const newDescription = ref('')
const adding = ref(false)
const error = ref('')

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
    error.value = 'Erreur lors de l\'ajout de la source.'
  } finally {
    adding.value = false
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
        <div class="source-info">
          <a :href="source.url" target="_blank" rel="noopener noreferrer" class="source-name">
            {{ source.name }}
          </a>
          <span v-if="source.description" class="source-desc"> · {{ source.description }}</span>
        </div>
        <span class="source-url">{{ source.url }}</span>
      </div>

      <div v-if="sources.length === 0" class="empty">
        Aucune source configurée.
      </div>
    </div>

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
      <div v-if="error" class="error">{{ error }}</div>
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
  flex-direction: column;
  gap: 2px;
  padding: 10px 0;
  border-bottom: 1px solid #e8e8e8;
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
}

.empty {
  padding: 24px 0;
  color: #aaa;
  font-size: 13px;
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

.error {
  margin-top: 8px;
  font-size: 12px;
  color: #c42b2b;
}
</style>
