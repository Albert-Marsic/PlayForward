import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { useTalkJS } from "@/context/TalkJSContext";
import { Button } from "@/components/ui/button";
import Talk from "talkjs";
import { Session, Inbox } from "@talkjs/react";
import { MessageCircle, ArrowLeft } from "lucide-react";

// TalkJS has a 2048 char limit for photoUrl
function sanitizePhotoUrl(url) {
  if (!url) return undefined;
  if (url.length > 2048) return undefined;
  return url;
}

export default function ChatPage() {
  const { user, loading: authLoading } = useAuth();
  const { appId, currentUser, isReady } = useTalkJS();
  const navigate = useNavigate();

  if (!authLoading && !user) {
    navigate("/prijava");
    return null;
  }

  const syncUser = () => new Talk.User({
    id: currentUser.id,
    name: currentUser.name,
    email: currentUser.email,
    photoUrl: sanitizePhotoUrl(currentUser.photoUrl),
    role: currentUser.role,
    signature: currentUser.signature,
  });

  if (authLoading) {
    return (
      <div className="flex items-center justify-center h-[calc(100vh-64px)]">
        <p className="text-gray-500">Ucitavanje...</p>
      </div>
    );
  }

  if (!isReady || !currentUser) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-64px)] p-8">
        <MessageCircle className="h-16 w-16 mb-4 text-gray-300" />
        <p className="text-gray-600 text-center">
          Morate biti prijavljeni za pregled poruka.
        </p>
        <Button
          onClick={() => navigate("/prijava")}
          className="mt-4 bg-green-600 hover:bg-green-700"
        >
          Prijava
        </Button>
      </div>
    );
  }

  return (
    <Session appId={appId} syncUser={syncUser}>
      <Inbox
        style={{ width: "100%", height: "calc(100vh - 64px)" }}
        showFeedHeader={true}
        showChatHeader={true}
      />
    </Session>
  );
}
