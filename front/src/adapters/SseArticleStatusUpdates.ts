import type { AuthService } from '../ports/AuthService'
import type { ArticleStatusUpdate, ArticleStatusUpdates } from '../ports/ArticleStatusUpdates'

export class SseArticleStatusUpdates implements ArticleStatusUpdates {
  private auth: AuthService

  constructor(auth: AuthService) {
    this.auth = auth
  }

  subscribe(onUpdate: (update: ArticleStatusUpdate) => void): () => void {
    const controller = new AbortController()
    this.stream(onUpdate, controller.signal)
    return () => controller.abort()
  }

  private async stream(
    onUpdate: (update: ArticleStatusUpdate) => void,
    signal: AbortSignal
  ): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
    try {
      const response = await fetch('/api/stream', { headers, signal })
      const reader = response.body!.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        const events = buffer.split('\n\n')
        buffer = events.pop()!
        for (const event of events) {
          const dataLine = event.split('\n').find(l => l.startsWith('data: '))
          if (dataLine) {
            try { onUpdate(JSON.parse(dataLine.slice(6))) } catch { /* JSON invalide ignoré */ }
          }
        }
      }
    } catch {
      // AbortError si disconnect volontaire → silencieux
    }
  }
}
