import type { Article, ArticleFilters } from '../domain/Article'

export interface ArticleRepository {
  getAll(filters?: ArticleFilters): Promise<Article[]>
}
