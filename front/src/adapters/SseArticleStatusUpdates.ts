import type { AuthService } from '../ports/AuthService'
import type { ArticleStatusUpdate, ArticleStatusUpdates } from '../ports/ArticleStatusUpdates'

const RECONNECT_DELAY_MS = 3_000

export class SseArticleStatusUpdates implements ArticleStatusUpdates {
  private auth: AuthService

  constructor(auth: AuthService) {
    this.auth = auth
  }

  subscribe(onUpdate: (update: ArticleStatusUpdate) => void): () => void {
    const controller = new AbortController()
    this.loop(onUpdate, controller.signal)
    return () => controller.abort()
  }

  private async loop(
    onUpdate: (update: ArticleStatusUpdate) => void,
    signal: AbortSignal
  ): Promise<void> {
    while (!signal.aborted) {
      try {
        await this.connect(onUpdate, signal)
      } catch {
        // connexion perdue ou AbortError
      }
      if (!signal.aborted) {
        await new Promise(resolve => setTimeout(resolve, RECONNECT_DELAY_MS))
      }
    }
  }

  private async connect(
    onUpdate: (update: ArticleStatusUpdate) => void,
    signal: AbortSignal
  ): Promise<void> {
    const headers = await this.auth.getAuthHeaders()
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
        const dataLine = event.split('\n').find(l => l.startsWith('data:'))
        if (dataLine) {
          const json = dataLine.startsWith('data: ') ? dataLine.slice(6) : dataLine.slice(5)
          try { onUpdate(JSON.parse(json)) } catch { /* JSON invalide ignoré */ }
        }
        // les lignes ": keepalive" sont silencieusement ignorées
      }
    }
  }
}
