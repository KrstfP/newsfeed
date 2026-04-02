import type { SourceRepository } from '../ports/SourceRepository'
import type { Source } from '../domain/Source'
import type { AuthService } from '../ports/AuthService'

export class NewsfeedSourceRepository implements SourceRepository {
  private auth: AuthService
  constructor(auth: AuthService) { this.auth = auth }

  async getAll(): Promise<Source[]> {
    const headers = await this.auth.getAuthHeaders()
    const res = await fetch('/api/sources', { headers })
    const data = await res.json()
    return data.map((item: any) => ({
      id: item.id,
      name: item.name,
      url: item.url,
      description: item.description,
    }))
  }

  async add(url: string, name: string, description?: string): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
    await fetch('/api/sources', {
      method: 'POST',
      headers: { ...headers, 'Content-Type': 'application/json' },
      body: JSON.stringify({ url, name, description }),
    })
  }

  async delete(id: string): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
    const res = await fetch(`/api/sources/${id}`, {
      method: 'DELETE',
      headers,
    })
    if (res.status === 404) throw new Error('Source introuvable.')
  }
}
