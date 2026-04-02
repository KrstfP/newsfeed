import type { SourceRepository } from '../ports/SourceRepository'

export async function getSources(repo: SourceRepository) {
  return repo.getAll()
}
