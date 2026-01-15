import axios from "axios";
import { API_BASE_URL } from "../lib/config";

/**
 * Kreira zahtjev za sve igračke u košarici
 * Poziva /api/igracke/{id}/rezerviraj za svaku igračku
 */
export async function createCheckoutRequest(toyIds) {
  if (!toyIds || toyIds.length === 0) {
    throw new Error("Nema igračaka za rezervaciju");
  }

  try {
    // Rezerviraj sve igračke paralelno
    const promises = toyIds.map(id =>
      axios.post(`http://${API_BASE_URL}/api/igracke/${id}/rezerviraj`, {})
    );

    const results = await Promise.all(promises);
    return results.map(res => res.data);
  } catch (err) {
    console.error("Greška pri kreiranju zahtjeva:", err);
    throw err;
  }
}
