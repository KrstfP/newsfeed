import type { ArticleRepository } from '../ports/ArticleRepository'
import type { Article, ArticleFilters } from '../domain/Article'
import type { RequestAnalysis } from '../ports/RequestAnalysis'
import type { AuthService } from '../ports/AuthService'

export class NewsfeedArticleRepository implements ArticleRepository, RequestAnalysis {
  private auth: AuthService
  constructor(auth: AuthService) { this.auth = auth }

  async requestAnalysis(article: Article): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
    await fetch(`/api/article/${article.id}/analyze`, { headers })
  }

  async getAll(filters: ArticleFilters = {}): Promise<Article[]> {
    const headers = await this.auth.getAuthHeaders()
    const params = new URLSearchParams()
    if (filters.analyzed !== undefined) params.set('analyzed', String(filters.analyzed))
    if (filters.since) params.set('since', filters.since)
    const url = '/api/articles' + (params.size ? '?' + params : '')
    const res = await fetch(url, { headers })
    const data = await res.json()
    return data.map((item: any) => ({
      id: item.id,
      title: item.title,
      url: item.url,
      source: item.source,
      description: item.content,
      categories: item.categories,
      analysisRequestStatus: item.analysisRequestStatus,
      analysis: item.analysis ?? null,
      publishedAt: new Date(item.publishedAt),
    }))
  }
}
