import { useEffect, useRef, useState } from "react";
import { Button } from "@/components/ui/button";
import { getMessages, sendMessage, markConversationAsRead } from "@/api/messages";
import { Send } from "lucide-react";

export default function ChatWindow({ conversationId, currentUser, loading }) {
  const [messages, setMessages] = useState([]);
    const [messageText, setMessageText] = useState("");
  const [sending, setSending] = useState(false);
  const [messageLoading, setMessageLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Učitaj poruke kada se odaberi razgovor
  useEffect(() => {
    if (!conversationId) return;

    const fetchMessages = async () => {
      try {
        setMessageLoading(true);
        const data = await getMessages(conversationId);
        setMessages(data);
        // Označi kao pročitano
        await markConversationAsRead(conversationId);
      } catch (err) {
        console.error("Greška pri učitavanju poruka:", err);
      } finally {
        setMessageLoading(false);
      }
    };

    fetchMessages();
  }, [conversationId]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!messageText.trim()) return;

    try {
      setSending(true);
      const newMessage = await sendMessage(conversationId, messageText);
      setMessages([...messages, newMessage]);
      setMessageText("");
    } catch (err) {
      alert("Greška pri slanju poruke");
    } finally {
      setSending(false);
    }
  };

  if (loading || !conversationId) {
    return (
      <div className="flex-1 flex items-center justify-center text-gray-500">
        <p>Odaberite razgovor</p>
      </div>
    );
  }

  if (messageLoading) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <p>Učitavanje poruka...</p>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col bg-white">
      {/* Poruke */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.length === 0 ? (
          <div className="text-center text-gray-500 mt-8">
            <p>Nema poruka. Počni konverzaciju!</p>
          </div>
        ) : (
          messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex ${
                msg.korisnikId === currentUser?.id
                  ? "justify-end"
                  : "justify-start"
              }`}
            >
              <div
                className={`max-w-xs px-4 py-2 rounded-lg ${
                  msg.korisnikId === currentUser?.id
                    ? "bg-blue-600 text-white rounded-br-none"
                    : "bg-gray-200 text-gray-900 rounded-bl-none"
                }`}
              >
                <p className="text-sm">{msg.tekst}</p>
                <p className="text-xs mt-1 opacity-70">
                  {new Date(msg.datumVremena).toLocaleTimeString("hr-HR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </p>
              </div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <form
        onSubmit={handleSendMessage}
        className="border-t p-4 flex gap-2"
      >
        <input
          type="text"
          placeholder="Napišite poruku..."
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          disabled={sending}
          className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
        />
        <Button
          type="submit"
          disabled={sending || !messageText.trim()}
          className="bg-blue-600 hover:bg-blue-700 px-4"
        >
          <Send size={18} />
        </Button>
      </form>
    </div>
  );
}
