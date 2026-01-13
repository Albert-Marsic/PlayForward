// Default to deployed backend; can be overridden with `VITE_API_BASE_URL` env var
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "https://playforward-backend-e5bg.onrender.com";

export const GOOGLE_LOGIN_URL = `${API_BASE_URL}/oauth2/authorization/google`;

export const AUTH_ENDPOINTS = {
  profile: `${API_BASE_URL}/api/auth/me`,
  logout: `${API_BASE_URL}/api/auth/logout`,
};
