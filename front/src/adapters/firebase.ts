import { initializeApp } from 'firebase/app'
import { getAuth } from 'firebase/auth'

// Firebase est initialisé uniquement en mode production.
// En mode dev, cet objet existe mais n'est jamais utilisé.
const isDev = import.meta.env.VITE_AUTH_MODE === 'dev'

export const auth = isDev
  ? (null as any)
  : getAuth(initializeApp({
      apiKey:     import.meta.env.VITE_FIREBASE_API_KEY,
      authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
      projectId:  import.meta.env.VITE_FIREBASE_PROJECT_ID,
    }))
