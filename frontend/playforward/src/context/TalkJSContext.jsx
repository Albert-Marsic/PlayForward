import { createContext, useContext, useEffect, useState } from "react";
import { useAuth } from "./AuthContext";
import { api } from "@/lib/api";
import { TALKJS_APP_ID } from "@/lib/config";

const TalkJSContext = createContext({
  appId: null,
  currentUser: null,
  isReady: false,
});

// TalkJS has a 2048 char limit for photoUrl
function sanitizePhotoUrl(url) {
  if (!url) return undefined;
  if (url.length > 2048) return undefined;
  return url;
}

export function TalkJSProvider({ children }) {
  const { user, loading: authLoading } = useAuth();
  const [signature, setSignature] = useState(null);
  const [signatureLoading, setSignatureLoading] = useState(false);

  // Fetch TalkJS signature from backend when user is authenticated
  useEffect(() => {
    if (authLoading || !user) {
      setSignature(null);
      return;
    }

    const fetchSignature = async () => {
      setSignatureLoading(true);
      try {
        const response = await api("/talkjs/signature");
        if (response.ok) {
          const data = await response.json();
          setSignature(data.signature);
        } else {
          console.error("Failed to fetch TalkJS signature");
          setSignature(null);
        }
      } catch (error) {
        console.error("Error fetching TalkJS signature:", error);
        setSignature(null);
      } finally {
        setSignatureLoading(false);
      }
    };

    fetchSignature();
  }, [user, authLoading]);

  // Build the TalkJS user object from auth user
  const currentUser = user ? {
    id: user.email,
    name: user.name || user.email,
    email: user.email,
    photoUrl: sanitizePhotoUrl(user.picture),
    role: user.role || "default",
    signature: signature, // Include signature for identity verification
  } : null;

  const isReady = !authLoading && !signatureLoading && !!user && !!signature;

  return (
    <TalkJSContext.Provider value={{
      appId: TALKJS_APP_ID,
      currentUser,
      isReady,
    }}>
      {children}
    </TalkJSContext.Provider>
  );
}

export function useTalkJS() {
  return useContext(TalkJSContext);
}

// Helper to create conversation ID for a toy chat
export function createConversationId(toyId, userEmail1, userEmail2) {
  const participants = [userEmail1, userEmail2].sort().join("_");
  return `toy_${toyId}_${participants}`;
}
