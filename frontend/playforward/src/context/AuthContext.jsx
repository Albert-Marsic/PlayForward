import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { AUTH_ENDPOINTS } from "@/lib/config";

const AuthContext = createContext({
  user: null,
  loading: true,
  refresh: () => {},
  logout: async () => {},
});

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchProfile = useCallback(async () => {
    try {
      const response = await fetch(AUTH_ENDPOINTS.profile, {
        credentials: "include",
      });

      if (!response.ok) {
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
        setUser(null);
      }
    } catch (error) {
      console.error("Failed to fetch profile", error);
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  const logout = useCallback(async () => {
    try {
      await fetch(AUTH_ENDPOINTS.logout, {
        method: "POST",
        credentials: "include",
      });
    } catch (error) {
      console.error("Logout failed", error);
    } finally {
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
