import type { SourceRepository } from '../ports/SourceRepository'
import type { Source } from '../domain/Source'

export class NewsfeedSourceRepository implements SourceRepository {
  async getAll(): Promise<Source[]> {
    const res = await fetch('http://localhost:8080/api/sources')
    const data = await res.json()
    return data.map((item: any) => ({
      id: item.id,
      name: item.name,
      url: item.url,
      description: item.description,
    }))
  }

  async add(url: string, name: string, description?: string): Promise<void> {
    await fetch('http://localhost:8080/api/sources', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ url, name, description }),
    })
  }
}
