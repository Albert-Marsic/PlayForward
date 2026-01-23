import { api } from "../lib/api";

/**
 * Kreira zahtjev za sve igračke u košarici
 * Poziva /api/zahtjevi za svaku igračku
 */
export async function createCheckoutRequest(toyIds) {
  if (!toyIds || toyIds.length === 0) {
    throw new Error("Nema igračaka za rezervaciju");
  }

  try {
    const requests = toyIds.map(id =>
      api("/zahtjevi", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ igrackaId: id })
      })
    );

    const responses = await Promise.all(requests);
    const failed = responses.find(response => !response.ok);
    if (failed) throw new Error("Greška pri kreiranju zahtjeva");

    return await Promise.all(responses.map(response => response.json()));
  } catch (err) {
    console.error("Greška pri kreiranju zahtjeva:", err);
    throw err;
  }
}
