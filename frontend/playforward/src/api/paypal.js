import { api } from "../lib/api";

/**
 * Kreiraj PayPal plaćanje
 */
export async function createPayPalPayment(amount, description, requestId) {
  if (!amount || !description) {
    throw new Error("Iznos i opis su obavezni");
  }

  try {
    const response = await api("/plaćanja/paypal/kreiraj", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        iznos: amount,
        opis: description,
        zahtjevId: requestId,
      }),
    });
    if (!response.ok) throw new Error("Greška pri kreiranju PayPal plaćanja");
    return await response.json();
  } catch (err) {
    console.error("Greška pri kreiranju PayPal plaćanja:", err);
    throw err;
  }
}

/**
 * Potvrdi PayPal plaćanje
 */
export async function executePayPalPayment(paymentId, payerId) {
  if (!paymentId || !payerId) {
    throw new Error("ID plaćanja i payer ID su obavezni");
  }

  try {
    const response = await api("/plaćanja/paypal/potvrdi", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        paymentId,
        payerId,
      }),
    });
    if (!response.ok) throw new Error("Greška pri potvrdi PayPal plaćanja");
    return await response.json();
  } catch (err) {
    console.error("Greška pri potvrdi PayPal plaćanja:", err);
    throw err;
  }
}

/**
 * Otkaži PayPal plaćanje
 */
export async function cancelPayPalPayment(paymentId) {
  if (!paymentId) throw new Error("ID plaćanja je obavezan");

  try {
    const response = await api(`/plaćanja/paypal/${paymentId}/otkaži`, {
      method: "POST",
    });
    if (!response.ok) throw new Error("Greška pri otkazivanju PayPal plaćanja");
    return await response.json();
  } catch (err) {
    console.error("Greška pri otkazivanju PayPal plaćanja:", err);
    throw err;
  }
}

/**
 * Dohvati status plaćanja
 */
export async function getPaymentStatus(paymentId) {
  if (!paymentId) throw new Error("ID plaćanja je obavezan");

  try {
    const response = await api(`/plaćanja/${paymentId}/status`);
    if (!response.ok) throw new Error("Greška pri dohvaćanju statusa plaćanja");
    return await response.json();
  } catch (err) {
    console.error("Greška pri dohvaćanju statusa plaćanja:", err);
    throw err;
  }
}

/**
 * Dohvati sve plaćanja korisnika
 */
export async function getUserPayments() {
  try {
    const response = await api("/plaćanja/moja");
    if (!response.ok) throw new Error("Greška pri dohvaćanju plaćanja");
    return await response.json() || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju plaćanja:", err);
    throw err;
  }
}
