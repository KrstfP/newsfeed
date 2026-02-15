import type { Article } from '../domain/Article'

export interface ArticleRepository {
  getAll(): Promise<Article[]>
}