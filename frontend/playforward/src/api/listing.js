import axios from "axios";
import { API_BASE_URL } from "../lib/config";

/**
 * Kreira novu igračku (donator je objavljuje)
 * @param {Object} toyData - { naziv, kategorija, stanje, fotografija, uvjeti }
 * @returns Promise
 */
export async function createToy(toyData) {
  if (!toyData.naziv || !toyData.kategorija || !toyData.stanje || !toyData.fotografija) {
    throw new Error("Naziv, kategorija, stanje i fotografija su obavezni");
  }

  try {
    const res = await axios.post(`http://${API_BASE_URL}/api/igracke`, {
      naziv: toyData.naziv.trim(),
      kategorija: toyData.kategorija.trim(),
      stanje: toyData.stanje,
      fotografija: toyData.fotografija.trim(),
      uvjeti: toyData.uvjeti ? toyData.uvjeti.trim() : null
    });

    return res.data;
  } catch (err) {
    console.error("Greška pri dodavanju igračke:", err);
    throw err;
  }
}

/**
 * Učitava igračku kao datoteku (image data URI ili URL)
 * Pretpostavlja da backend može primiti URL ili data URI slike
 */
export function fileToDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}
