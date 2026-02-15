import type { ArticleRepository } from '../ports/ArticleRepository'
import type { Article } from '../domain/Article'
import type { RequestAnalysis } from '../ports/RequestAnalysis'

export class NewsfeedArticleRepository implements ArticleRepository, RequestAnalysis {
  requestAnalysis(article: Article): Promise<void> {
    return fetch(`http://localhost:8080/api/article/${article.id}/analyze`).then(() => {})
  }
  async getAll(): Promise<Article[]> {
    const res = await fetch('http://localhost:8080/api/articles')
    const data = await res.json()

    return data.map((item: any) => ({
      id: item.id,
      title: item.title,
      url: item.url,
      source: item.source,
      description: item.content,
      categories: item.categories,
      analysisRequestStatus: item.analysisRequestStatus,
      publishedAt: new Date(item.publishedAt)
    }))
  }
}