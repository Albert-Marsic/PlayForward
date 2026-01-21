import { api } from "../lib/api";

/**
 * Dohvati sve razgovore korisnika
 */
export async function getConversations() {
  try {
    const response = await api("/poruke/razgovori");
    if (!response.ok) throw new Error("Greška pri dohvaćanju razgovora");
    return await response.json() || [];
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
    if (!response.ok) throw new Error("Greška pri dohvaćanju poruka");
    return await response.json() || [];
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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ tekst: text }),
    });
    if (!response.ok) throw new Error("Greška pri slanju poruke");
    return await response.json();
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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ drugKorisnikId: otherUserId }),
    });
    if (!response.ok) throw new Error("Greška pri kreiranju razgovora");
    return await response.json();
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
    if (!response.ok) throw new Error("Greška pri označavanju razgovora");
    return await response.json();
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
    if (!response.ok) throw new Error("Greška pri brisanju razgovora");
    return await response.json();
  } catch (err) {
    console.error("Greška pri brisanju razgovora:", err);
    throw err;
  }
}
