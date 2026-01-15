import { api } from "../lib/api";

/**
 * Dohvati sve razgovore korisnika
 */
export async function getConversations() {
  try {
    const response = await api("/poruke/razgovori");
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju razgovora:", err);
    throw err;
  }
}

/**
 * Dohvati poruke iz razgovora
 */
export async function getMessages(conversationId, limit = 50, offset = 0) {
  if (!conversationId) throw new Error("ID razgovora je obavezan");

  try {
    const response = await api(`/poruke/razgovori/${conversationId}?limit=${limit}&offset=${offset}`);
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju poruka:", err);
    throw err;
  }
}

/**
 * Pošalji novu poruku
 */
export async function sendMessage(conversationId, text) {
  if (!conversationId || !text) {
    throw new Error("ID razgovora i tekst poruke su obavezni");
  }

  try {
    const response = await api(`/poruke/razgovori/${conversationId}`, {
      method: "POST",
      data: { tekst: text },
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri slanju poruke:", err);
    throw err;
  }
}

/**
 * Kreiraj novi razgovor sa korisnikom
 */
export async function startConversation(otherUserId) {
  if (!otherUserId) throw new Error("ID drugog korisnika je obavezan");

  try {
    const response = await api("/poruke/razgovori", {
      method: "POST",
      data: { drugKorisnikId: otherUserId },
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri kreiranju razgovora:", err);
    throw err;
  }
}

/**
 * Označi razgovor kao pročitan
 */
export async function markConversationAsRead(conversationId) {
  if (!conversationId) throw new Error("ID razgovora je obavezan");

  try {
    const response = await api(`/poruke/razgovori/${conversationId}/procitano`, {
      method: "POST",
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri označavanju razgovora:", err);
    throw err;
  }
}

/**
 * Izbriši razgovor
 */
export async function deleteConversation(conversationId) {
  if (!conversationId) throw new Error("ID razgovora je obavezan");

  try {
    const response = await api(`/poruke/razgovori/${conversationId}`, {
      method: "DELETE",
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri brisanju razgovora:", err);
    throw err;
  }
}
