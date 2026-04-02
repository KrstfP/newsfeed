export const RequestStatus = {
  NOT_REQUESTED : "NOT_REQUESTED",
  PENDING : "PENDING",
  IN_PROGRESS : "IN_PROGRESS",
  COMPLETED : "COMPLETED",
  FAILED : "FAILED"
} as const

export type RequestStatus =
  typeof RequestStatus[keyof typeof RequestStatus]

export interface Article {
  id: string
  title: string
  url: string
  description: string
  source: string
  publishedAt: Date
  analysisRequestStatus: RequestStatus
  categories: [string]
  analysis: string | null
}