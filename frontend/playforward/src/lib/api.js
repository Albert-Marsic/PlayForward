import { API_BASE_URL } from "./config";

const TOKEN_KEY = "jwt_token";

/**
 * Get authentication headers with JWT token
 */
export function getAuthHeaders() {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    return {
      "Authorization": `Bearer ${token}`,
    };
  }
  return {};
}

/**
 * Authenticated API fetch - automatically includes JWT token and API base URL
 * Usage: api("/auth/me") or api("/toys", { method: "POST", body: JSON.stringify(data) })
 * Endpoint should start with / and will be appended to API_BASE_URL/api
 */
export async function api(endpoint, options = {}) {
  const url = `${API_BASE_URL}/api${endpoint}`;

  return fetch(url, {
    ...options,
    headers: {
      ...getAuthHeaders(),
      ...options.headers,
    },
  });
}
