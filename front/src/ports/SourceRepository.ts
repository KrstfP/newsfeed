import type { Source } from '../domain/Source'

export interface SourceRepository {
  getAll(): Promise<Source[]>
  add(url: string, name: string, description?: string): Promise<void>
  delete(id: string): Promise<void>
}
