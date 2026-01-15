import axios from "axios";
import { API_BASE_URL } from "../lib/config";
import { api } from "../lib/api";

/**
 * Pošalji recenziju za donatora
 * @param {Object} reviewData - { donatorId, ocjena, tekst }
 */
export async function submitReview(reviewData) {
  if (!reviewData.donatorId || !reviewData.ocjena || !reviewData.tekst) {
    throw new Error("Donator ID, ocjena i tekst su obavezni");
  }

  if (reviewData.ocjena < 1 || reviewData.ocjena > 5) {
    throw new Error("Ocjena mora biti između 1 i 5");
  }

  try {
    const response = await api("/recenzije", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        donatorId: reviewData.donatorId,
        ocjena: reviewData.ocjena,
        tekst: reviewData.tekst.trim()
      })
    });

    if (!response.ok) throw new Error("Greška pri slanju recenzije");
    return await response.json();
  } catch (err) {
    console.error("Greška pri slanju recenzije:", err);
    throw err;
  }
}

/**
 * Dohvati sve recenzije za donatora
 */
export async function getReviewsForDonator(donatorId) {
  if (!donatorId) {
    throw new Error("ID donatora je obavezan");
  }

  try {
    const response = await api(`/recenzije/donator/${donatorId}`);
    if (!response.ok) throw new Error("Greška pri dohvaćanju recenzija");
    return await response.json();
  } catch (err) {
    console.error("Greška pri dohvaćanju recenzija:", err);
    throw err;
  }
}

/**
 * Izračunaj prosječnu ocjenu
 */
export function calculateAverageRating(reviews) {
  if (!reviews || reviews.length === 0) return 0;
  const sum = reviews.reduce((acc, review) => acc + (review.ocjena || 0), 0);
  return (sum / reviews.length).toFixed(1);
}
