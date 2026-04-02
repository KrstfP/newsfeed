import type { AuthService } from '../ports/AuthService'
import { auth } from './firebase'

export class FirebaseAuthService implements AuthService {
  async getAuthHeaders(): Promise<Record<string, string>> {
    const token = await auth.currentUser?.getIdToken()
    if (!token) return {}
    return { 'Authorization': `Bearer ${token}` }
  }
}
