import type { ArticleRepository } from '../ports/ArticleRepository'
import type { Article } from '../domain/Article'
import type { RequestAnalysis } from '../ports/RequestAnalysis'
import type { AuthService } from '../ports/AuthService'

export class NewsfeedArticleRepository implements ArticleRepository, RequestAnalysis {
  private auth: AuthService
  constructor(auth: AuthService) { this.auth = auth }

  async requestAnalysis(article: Article): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
    await fetch(`/api/article/${article.id}/analyze`, { headers })
  }

  async getAll(): Promise<Article[]> {
    const headers = await this.auth.getAuthHeaders()
    const res = await fetch('/api/articles', { headers })
    const data = await res.json()
    return data.map((item: any) => ({
      id: item.id,
      title: item.title,
      url: item.url,
      source: item.source,
      description: item.content,
      categories: item.categories,
      analysisRequestStatus: item.analysisRequestStatus,
      publishedAt: new Date(item.publishedAt),
    }))
  }
}
