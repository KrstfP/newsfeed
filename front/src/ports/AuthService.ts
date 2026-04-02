export interface AuthService {
  getAuthHeaders(): Promise<Record<string, string>>
}
