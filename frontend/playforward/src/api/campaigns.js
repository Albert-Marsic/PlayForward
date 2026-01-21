import { api } from "../lib/api";

/**
 * Dohvati sve kampanje
 */
export async function getCampaigns() {
  try {
    const response = await api("/kampanje");
    if (!response.ok) throw new Error("Greška pri dohvaćanju kampanja");
    return await response.json() || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju kampanja:", err);
    throw err;
  }
}

/**
 * Dohvati detalje kampanje
 */
export async function getCampaignDetails(campaignId) {
  if (!campaignId) throw new Error("ID kampanje je obavezan");
  
  try {
    const response = await api(`/kampanje/${campaignId}`);
    if (!response.ok) throw new Error("Greška pri dohvaćanju kampanje");
    return await response.json() || null;
  } catch (err) {
    console.error("Greška pri dohvaćanju kampanje:", err);
    throw err;
  }
}

/**
 * Pošalji zahtjev za igračku iz kampanje
 */
export async function requestToyFromCampaign(campaignId, toyId) {
  if (!campaignId || !toyId) {
    throw new Error("ID kampanje i igračke su obavezni");
  }

  try {
    const response = await api(`/kampanje/${campaignId}/igracke/${toyId}/zahtjev`, {
      method: "POST"
    });
    if (!response.ok) throw new Error("Greška pri slanju zahtjeva");
    return await response.json();
  } catch (err) {
    console.error("Greška pri slanju zahtjeva:", err);
    throw err;
  }
}

/**
 * Izračunaj procenat prikupljenih igračaka
 */
export function calculateCompletionPercentage(kampanja) {
  if (!kampanja || !kampanja.popisi || kampanja.popisi.length === 0) return 0;
  
  const total = kampanja.popisi.reduce((sum, popis) => sum + popis.kolicina, 0);
  const completed = kampanja.popisi.reduce((sum, popis) => {
    return sum + (popis.status === "PRIKUPLJENO" ? popis.kolicina : 0);
  }, 0);
  
  return total > 0 ? Math.round((completed / total) * 100) : 0;
}
