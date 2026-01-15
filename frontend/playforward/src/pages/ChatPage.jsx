import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { getConversations, startConversation } from "@/api/messages";
import ChatList from "@/components/ChatList";
import ChatWindow from "@/components/ChatWindow";
import { Button } from "@/components/ui/button";
import { MessageCircle, ArrowLeft } from "lucide-react";

export default function ChatPage() {
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    if (!authLoading && !user) {
      navigate("/prijava");
      return;
    }

    const fetchConversations = async () => {
      try {
        setLoading(true);
        const data = await getConversations();
        setConversations(data);
        if (data.length > 0) {
          setSelectedConversation(data[0].id);
        }
        setError(null);
      } catch (err) {
        console.error("Greška pri dohvaćanju razgovora:", err);
        setError("Nije moguće učitati razgovore");
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchConversations();
    }
  }, [user, authLoading, navigate]);

  const handleRefresh = async () => {
    try {
      setRefreshing(true);
      const data = await getConversations();
      setConversations(data);
    } catch (err) {
      console.error("Greška pri osvežavanju:", err);
    } finally {
      setRefreshing(false);
    }
  };

  const handleNewConversation = async () => {
    const userId = prompt("Unesite email korisnika sa kojim želite da razgovarate:");
    if (!userId) return;

    try {
      const newConv = await startConversation(userId);
      setConversations([newConv, ...conversations]);
      setSelectedConversation(newConv.id);
    } catch (err) {
      alert("Greška pri kreiranju razgovora: " + err.message);
    }
  };

  if (authLoading || loading) {
    return <div className="p-6 text-center">Učitavanje...</div>;
  }

  if (!user) {
    return (
      <div className="p-6 text-center">
        <p className="text-red-600 mb-4">Morate biti prijavljeni</p>
        <Button onClick={() => navigate("/prijava")}>Prijava</Button>
      </div>
    );
  }

  return (
    <div className="flex h-[calc(100vh-64px)]">
      {/* Chat lista */}
      <div className="relative">
        <ChatList
          conversations={conversations}
          selectedId={selectedConversation}
          onSelect={setSelectedConversation}
          loading={false}
        />
        <div className="absolute bottom-4 left-4 right-4 flex gap-2">
          <Button
            onClick={handleRefresh}
            disabled={refreshing}
            variant="outline"
            className="flex-1"
          >
            Osvežiti
          </Button>
          <Button
            onClick={handleNewConversation}
            className="flex-1 bg-green-600 hover:bg-green-700"
          >
            <MessageCircle size={16} className="mr-2" />
            Nova
          </Button>
        </div>
      </div>

      {/* Chat prozor */}
      <ChatWindow
        conversationId={selectedConversation}
        currentUser={user}
        loading={false}
      />

      {/* Error prikaz */}
      {error && (
        <div className="fixed bottom-4 right-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}
    </div>
  );
}
