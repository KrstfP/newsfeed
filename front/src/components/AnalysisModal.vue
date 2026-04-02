<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'
import type { Article } from '../domain/Article'
import { NModal, NTag } from 'naive-ui'

const props = defineProps<{
  article: Article | null
  show: boolean
}>()

const emit = defineEmits<{ (e: 'close'): void }>()

const formattedDate = computed(() => {
  if (!props.article) return ''
  return new Intl.DateTimeFormat('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    .format(new Date(props.article.publishedAt))
})

const renderedAnalysis = computed(() => {
  if (!props.article?.analysis) return ''
  return marked(props.article.analysis) as string
})

function onUpdateShow(val: boolean) {
  if (!val) emit('close')
}
</script>

<template>
  <NModal
    :show="show"
    @update:show="onUpdateShow"
    :mask-closable="true"
    :close-on-esc="true"
    style="width: 90vw; max-width: 1100px; height: 88vh; display: flex; flex-direction: column; border-radius: 8px; overflow: hidden;"
  >
    <div class="modal-layout">
      <!-- Header -->
      <div class="modal-header">
        <div class="header-meta">
          <a
            :href="article?.url"
            target="_blank"
            rel="noopener noreferrer"
            class="source-link"
          >{{ article?.source }}</a>
          <span class="meta-sep"> · </span>
          <span class="meta-date">{{ formattedDate }}</span>
          <NTag size="small" type="success" :bordered="false" class="badge">Analyse complète</NTag>
        </div>
        <h2 class="modal-title">{{ article?.title }}</h2>
      </div>

      <!-- Body -->
      <div class="modal-body">
        <div class="markdown-content" v-html="renderedAnalysis" />
      </div>
    </div>
  </NModal>
</template>

<style scoped>
.modal-layout {
  display: flex;
  flex-direction: column;
  height: 88vh;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.modal-header {
  flex-shrink: 0;
  padding: 24px 36px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-bottom: 10px;
}

.source-link {
  font-size: 11px;
  color: #aaa;
  text-decoration: none;
}
.source-link:hover {
  text-decoration: underline;
  color: #888;
}

.meta-sep {
  font-size: 11px;
  color: #ccc;
}

.meta-date {
  font-size: 11px;
  color: #aaa;
}

.badge {
  margin-left: 10px;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e1e1e;
  line-height: 1.5;
  margin: 0;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 28px 36px 36px;
}
</style>

<style>
/* Markdown typography — non-scoped for v-html targeting */
.markdown-content {
  font-size: 13px;
  color: #2a2a2a;
  line-height: 1.7;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3 {
  font-weight: 600;
  color: #1e1e1e;
  margin: 1.4em 0 0.5em;
}

.markdown-content h1 { font-size: 16px; }
.markdown-content h2 { font-size: 14px; border-bottom: 1px solid #f0f0f0; padding-bottom: 6px; }
.markdown-content h3 { font-size: 13px; }

.markdown-content p {
  margin: 0 0 0.8em;
}

.markdown-content ul,
.markdown-content ol {
  padding-left: 1.4em;
  margin: 0 0 0.8em;
}

.markdown-content li {
  margin-bottom: 4px;
}

.markdown-content strong {
  font-weight: 600;
  color: #1e1e1e;
}

.markdown-content em {
  color: #555;
}

.markdown-content hr {
  border: none;
  border-top: 1px solid #e8e8e8;
  margin: 1.2em 0;
}

.markdown-content blockquote {
  border-left: 3px solid #e0e0e0;
  margin: 0.8em 0;
  padding: 4px 16px;
  color: #666;
}
</style>
