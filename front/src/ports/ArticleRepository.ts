import type { ArticleFilters, PagedArticles } from '../domain/Article'

export interface ArticleRepository {
  getPage(filters?: ArticleFilters): Promise<PagedArticles>
}
