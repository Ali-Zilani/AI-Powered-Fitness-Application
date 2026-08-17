import { useAuthContext, AuthProvider, TAuthConfig, TRefreshTokenExpiredEvent } from "react-oauth2-code-pkce"

export const authConfig =  {
  clientId: 'oauth2-pkce-client',
  authorizationEndpoint: 'https://keycloak-production-53c1.up.railway.app/realms/fitness-app/protocol/openid-connect/auth',
  tokenEndpoint: 'https://keycloak-production-53c1.up.railway.app/realms/fitness-app/protocol/openid-connect/token',
  redirectUri: 'http://localhost:5173/',
  scope: 'openid profile email offline_access',
  onRefreshTokenExpire: (event) => event.logIn(),
}