import { api } from "../lib/api";

async function parseResponse(response) {
  const text = await response.text();
  let payload = null;

  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = text;
    }
  }

  if (!response.ok) {
    const message =
      payload && typeof payload === "object"
        ? payload.message || payload.error
        : payload;
    throw new Error(message || `HTTP ${response.status}`);
  }

  return payload;
}

/**
 * Dohvati sve kampanje
 */
export async function getCampaigns() {
  try {
    const response = await api("/kampanje");
    const data = await parseResponse(response);
    return data || [];
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
    const data = await parseResponse(response);
    return data || null;
  } catch (err) {
    console.error("Greška pri dohvaćanju kampanje:", err);
    throw err;
  }
}

/**
 * Kreiraj novu kampanju
 */
export async function createCampaign(payload) {
  try {
    const response = await api("/kampanje", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload || {})
    });
    return await parseResponse(response);
  } catch (err) {
    console.error("Greška pri kreiranju kampanje:", err);
    throw err;
  }
}

/**
 * Spremi popis igračaka za kampanju
 */
export async function saveCampaignToyList(campaignId, items) {
  if (!campaignId) throw new Error("ID kampanje je obavezan");

  try {
    const response = await api(`/kampanje/${campaignId}/popis`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(items || [])
    });
    return await parseResponse(response);
  } catch (err) {
    console.error("Greška pri spremanju popisa igračaka:", err);
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
    const safeId = encodeURIComponent(toyId);
    const response = await api(`/kampanje/${campaignId}/igracke/${safeId}/zahtjev`, {
      method: "POST"
    });
    return await parseResponse(response);
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
    return sum + (popis.status === "DONIRANO" ? popis.kolicina : 0);
  }, 0);
  
  return total > 0 ? Math.round((completed / total) * 100) : 0;
}
