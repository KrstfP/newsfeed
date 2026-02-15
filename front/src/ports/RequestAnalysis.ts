import type { Article } from '../domain/Article'

export interface RequestAnalysis {
  requestAnalysis(article: Article): Promise<void>
}