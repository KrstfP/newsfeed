<script setup lang="ts">
import { ref } from 'vue'
import type { Article } from '../domain/Article'
import { RequestStatus } from '../domain/Article'
import { NButton, NIcon, NTag } from 'naive-ui'
import {
  BrainCircuit20Filled,
  Clock20Regular,
  ArrowSync20Regular,
  CheckmarkCircle20Regular,
  DismissCircle20Regular,
} from '@vicons/fluent'

const props = defineProps<{ article: Article }>()
const emit = defineEmits<{
  (e: 'analyze', article: Article): void
  (e: 'open-analysis', article: Article): void
}>()

const expanded = ref(false)

function onContentClick() {
  if (props.article.analysisRequestStatus === RequestStatus.COMPLETED) {
    emit('open-analysis', props.article)
  } else {
    expanded.value = !expanded.value
  }
}

const statusMap = {
  NOT_REQUESTED: { icon: BrainCircuit20Filled,    type: 'default'  as const, loading: false },
  PENDING:       { icon: Clock20Regular,           type: 'warning'  as const, loading: false },
  IN_PROGRESS:   { icon: ArrowSync20Regular,       type: 'info'     as const, loading: true  },
  COMPLETED:     { icon: CheckmarkCircle20Regular, type: 'success'  as const, loading: false },
  FAILED:        { icon: DismissCircle20Regular,   type: 'error'    as const, loading: false },
}

const tagColors: Record<string, 'default' | 'primary' | 'success' | 'info' | 'warning' | 'error'> = {
  Tech: 'primary', Business: 'success', AI: 'info', Security: 'error',
}

const status = () => statusMap[props.article.analysisRequestStatus] ?? statusMap.NOT_REQUESTED

const formattedDate = new Intl.DateTimeFormat('fr-FR', {
  day: '2-digit', month: '2-digit',
}).format(new Date(props.article.publishedAt))
</script>

<template>
  <div class="row" :class="{ expanded }">
    <!-- Source → link to article -->
    <a
      :href="article.url"
      target="_blank"
      rel="noopener noreferrer"
      class="source"
      @click.stop
    >{{ article.source }}</a>

    <!-- Analysis icon button -->
    <NButton
      class="icon-btn"
      size="tiny"
      :type="status().type"
      :loading="status().loading"
      :disabled="status().loading"
      circle
      tertiary
      @click.stop="emit('analyze', article)"
    >
      <template #icon>
        <NIcon><component :is="status().icon" /></NIcon>
      </template>
    </NButton>

    <!-- Content: collapsed = single line, expanded = full -->
    <div class="content" @click="onContentClick">
      <template v-if="!expanded">
        <span class="title">{{ article.title }}</span>
        <span class="sep"> · </span>
        <span class="desc">{{ article.description }}</span>
      </template>
      <template v-else>
        <div class="title title-block">{{ article.title }}</div>
        <div class="desc desc-full">{{ article.description }}</div>
        <div class="tags">
          <NTag
            v-for="cat in article.categories"
            :key="cat"
            size="small"
            :bordered="false"
            :type="tagColors[cat] ?? 'default'"
          >{{ cat }}</NTag>
        </div>
      </template>
    </div>

    <!-- Date -->
    <span class="date">{{ formattedDate }}</span>
  </div>
  <div class="row-sep" />
</template>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 108px 26px 1fr 65px;
  gap: 8px;
  align-items: center;
  padding: 6px 0;
  min-width: 0;
}

.row.expanded {
  align-items: start;
  background: #f1f4ff;
  border-radius: 4px;
  padding: 6px 6px;
  margin: 0 -6px;
}

.row-sep {
  height: 1px;
  background: #e8e8e8;
}

/* Source */
.source {
  font-size: 11px;
  color: #aaa;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.source:hover {
  text-decoration: underline;
  color: #888;
}

/* Icon button — fixed size via NButton tiny+circle */
.icon-btn {
  flex-shrink: 0;
}

/* Content cell */
.content {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
  min-width: 0;
  font-size: 12px;
  line-height: 1.4;
}

.row.expanded .content {
  white-space: normal;
  overflow: visible;
}

.title {
  font-weight: 600;
  color: #1e1e1e;
  font-size: 12px;
}

.sep {
  color: #bbb;
}

.desc {
  color: #999;
  font-size: 11px;
}

.title-block {
  display: block;
  margin-bottom: 4px;
}

.desc-full {
  display: block;
  color: #666;
  font-size: 11px;
  margin-bottom: 6px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

@media (max-width: 768px) {
  .row {
    grid-template-columns: 26px 1fr 55px;
  }

  .source {
    display: none;
  }
}

/* Date */
.date {
  font-size: 10px;
  color: #aaa;
  text-align: right;
  white-space: nowrap;
}

.row.expanded .date {
  padding-top: 2px;
}
</style>
