import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { deleteConversation, markConversationAsRead } from "@/api/messages";
import { Trash2, Mail } from "lucide-react";

export default function ChatList({
  conversations,
  selectedId,
  onSelect,
  loading,
}) {
  const [deleting, setDeleting] = useState(null);

  const handleDelete = async (conversationId, e) => {
    e.stopPropagation();
    if (!window.confirm("Obrisati razgovor?")) return;

    try {
      setDeleting(conversationId);
      await deleteConversation(conversationId);
      // Osvežavanje liste se dešava u parent komponenti
    } catch (err) {
      alert("Greška pri brisanju razgovora");
    } finally {
      setDeleting(null);
    }
  };

  if (loading) {
    return (
      <div className="bg-white border-r h-screen flex items-center justify-center">
        <p>Učitavanje razgovora...</p>
      </div>
    );
  }

  return (
    <div className="bg-white border-r h-screen overflow-y-auto w-80">
      <div className="p-4 border-b sticky top-0 bg-white">
        <h2 className="text-lg font-semibold">Poruke</h2>
      </div>

      {conversations.length === 0 ? (
        <div className="p-4 text-center text-gray-500">
          <Mail size={32} className="mx-auto mb-2 opacity-50" />
          <p>Nema aktivnih razgovora</p>
        </div>
      ) : (
        <div className="space-y-1 p-2">
          {conversations.map((conv) => (
            <div
              key={conv.id}
              onClick={() => onSelect(conv.id)}
              className={`p-3 rounded cursor-pointer transition flex items-center justify-between group ${
                selectedId === conv.id
                  ? "bg-blue-100 border-l-4 border-blue-600"
                  : "hover:bg-gray-100"
              }`}
            >
              <div className="flex-1 min-w-0">
                <p className="font-medium truncate">
                  {conv.drugKorisnik?.email || "Korisnik"}
                  {conv.neprocionoCount > 0 && (
                    <span className="ml-2 bg-red-500 text-white text-xs rounded-full px-2 py-0.5">
                      {conv.neprocionoCount}
                    </span>
                  )}
                </p>
                <p className="text-xs text-gray-600 truncate">
                  {conv.zadnjaPoruka?.tekst || "Nema poruka"}
                </p>
              </div>
              <button
                onClick={(e) => handleDelete(conv.id, e)}
                disabled={deleting === conv.id}
                className="opacity-0 group-hover:opacity-100 ml-2 p-1 hover:bg-red-100 rounded transition"
              >
                <Trash2 size={16} className="text-red-600" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
