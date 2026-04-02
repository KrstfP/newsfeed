export interface ArticleStatusUpdate {
  articleId: string
  objectType: string
  changeType: string
  oldValue: string
  newValue: string
}

export interface ArticleStatusUpdates {
  subscribe(onUpdate: (update: ArticleStatusUpdate) => void): () => void
}
