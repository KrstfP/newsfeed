import type { AuthService } from '../ports/AuthService'

export class DevAuthService implements AuthService {
  async getAuthHeaders(): Promise<Record<string, string>> {
    return { 'X-User-Id': import.meta.env.VITE_DEV_USER_ID ?? 'dev-user' }
  }
}
