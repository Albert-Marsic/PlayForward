import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { api } from "@/lib/api";

const AuthContext = createContext({
  user: null,
  loading: true,
  refresh: () => {},
  logout: async () => {},
});

const TOKEN_KEY = "jwt_token";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();

  const fetchProfile = useCallback(async () => {
    const token = localStorage.getItem(TOKEN_KEY);

    if (!token) {
      setUser(null);
      setLoading(false);
      return;
    }

    try {
      const response = await api("/auth/me");

      if (!response.ok) {
        // Token is invalid or expired
        localStorage.removeItem(TOKEN_KEY);
        setUser(null);
        return;
      }

      const data = await response.json();
      if (data.authenticated) {
        setUser({
          name: data.name ?? data.email,
          email: data.email,
          picture: data.picture,
        });
      } else {
        localStorage.removeItem(TOKEN_KEY);
        setUser(null);
      }
    } catch (error) {
      console.error("Failed to fetch profile", error);
      localStorage.removeItem(TOKEN_KEY);
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  // Check for token in URL parameters (from OAuth redirect)
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (token) {
      // Store the token
      localStorage.setItem(TOKEN_KEY, token);

      // Remove token from URL
      params.delete("token");
      const newSearch = params.toString();
      const newUrl = `${location.pathname}${newSearch ? `?${newSearch}` : ""}`;
      navigate(newUrl, { replace: true });

      // Fetch user profile
      fetchProfile();
    }
  }, [location, navigate, fetchProfile]);

  useEffect(() => {
    // Only fetch profile if we haven't just processed a token from URL
    const params = new URLSearchParams(location.search);
    if (!params.get("token")) {
      fetchProfile();
    }
  }, [fetchProfile, location.search]);

  const logout = useCallback(async () => {
    try {
      await api("/auth/logout", { method: "POST" });
    } catch (error) {
      console.error("Logout failed", error);
    } finally {
      // Always remove token and clear user state
      localStorage.removeItem(TOKEN_KEY);
      setUser(null);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        refresh: fetchProfile,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
