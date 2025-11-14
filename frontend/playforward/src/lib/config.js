export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const GOOGLE_LOGIN_URL = `${API_BASE_URL}/oauth2/authorization/google`;

export const AUTH_ENDPOINTS = {
  profile: `${API_BASE_URL}/api/auth/me`,
  logout: `${API_BASE_URL}/api/auth/logout`,
};
