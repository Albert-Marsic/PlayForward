import { useState, useCallback } from "react";
import Talk from "talkjs";
import { Session, Popup } from "@talkjs/react";
import { useTalkJS, createConversationId } from "@/context/TalkJSContext";
import { Button } from "@/components/ui/button";
import { MessageCircle } from "lucide-react";

// TalkJS has a 2048 char limit for photoUrl
function sanitizePhotoUrl(url) {
  if (!url) return undefined;
  if (url.length > 2048) return undefined;
  return url;
}

export default function ChatPopup({ toy, donator, onClose }) {
  const { appId, currentUser, isReady } = useTalkJS();
  const [isOpen, setIsOpen] = useState(true);

  const handleClose = useCallback(() => {
    setIsOpen(false);
    if (onClose) onClose();
  }, [onClose]);

  // Get donator email - handle both possible structures
  const donatorEmail = donator?.korisnik?.email || donator?.email;
  const donatorName = donator?.korisnik?.imeKorisnik || donator?.imeKorisnik || donatorEmail;

  if (!isReady || !currentUser) {
    return (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded-lg shadow-xl">
          <p>Morate biti prijavljeni za chat.</p>
          <Button onClick={handleClose} className="mt-4">Zatvori</Button>
        </div>
      </div>
    );
  }

  if (!donatorEmail) {
    return (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded-lg shadow-xl">
          <p>Nije moguće započeti chat - nedostaju podaci o donatoru.</p>
          <Button onClick={handleClose} className="mt-4">Zatvori</Button>
        </div>
      </div>
    );
  }

  const conversationId = createConversationId(toy.id, currentUser.email, donatorEmail);

  const syncUser = () => new Talk.User({
    id: currentUser.id,
    name: currentUser.name,
    email: currentUser.email,
    photoUrl: sanitizePhotoUrl(currentUser.photoUrl),
    role: currentUser.role,
    signature: currentUser.signature,
  });

  const syncConversation = (session) => {
    const conversation = session.getOrCreateConversation(conversationId);

    // Set current user as participant
    conversation.setParticipant(session.me);

    // Set donator as participant
    const otherUser = new Talk.User({
      id: donatorEmail,
      name: donatorName,
      email: donatorEmail,
    });
    conversation.setParticipant(otherUser);

    // Set conversation attributes
    conversation.setAttributes({
      subject: `Igračka: ${toy.naziv}`,
      photoUrl: sanitizePhotoUrl(toy.fotografija),
      custom: {
        toyId: String(toy.id),
        toyName: toy.naziv,
      },
    });

    return conversation;
  };

  return (
    <Session appId={appId} syncUser={syncUser}>
      <Popup
        syncConversation={syncConversation}
        onClose={handleClose}
        show={isOpen}
      />
    </Session>
  );
}

// Standalone chat button that can be placed anywhere
export function ChatButton({ toy, donator, disabled, className }) {
  const [showChat, setShowChat] = useState(false);
  const { isReady } = useTalkJS();

  // Get donator email - handle both possible structures
  const donatorEmail = donator?.korisnik?.email || donator?.email;

  // Don't render if not ready or no donator email
  if (!isReady || !donatorEmail) return null;

  return (
    <>
      <Button
        variant="outline"
        onClick={() => setShowChat(true)}
        disabled={disabled}
        className={className}
      >
        <MessageCircle className="h-4 w-4 mr-2" />
        Poruka donatoru
      </Button>

      {showChat && (
        <ChatPopup
          toy={toy}
          donator={donator}
          onClose={() => setShowChat(false)}
        />
      )}
    </>
  );
}
