import type { ArticleRepository } from '../ports/ArticleRepository'

export async function getArticles(repo: ArticleRepository) {
  const articles = await repo.getAll()

  return articles.sort(
    (a, b) => b.publishedAt.getTime() - a.publishedAt.getTime()
  )
}