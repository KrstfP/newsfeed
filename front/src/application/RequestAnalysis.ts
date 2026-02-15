import type { Article } from '../domain/Article'
import type { RequestAnalysis } from '../ports/RequestAnalysis'

export async function requestAnalysis(req: RequestAnalysis, article: Article) {
  await req.requestAnalysis(article)
}