import type { ArticleRepository } from '../ports/ArticleRepository'
import type { ArticleFilters } from '../domain/Article'

export async function getArticles(repo: ArticleRepository, filters?: ArticleFilters) {
  return repo.getPage(filters)
}
