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
      data: {
        iznos: amount,
        opis: description,
        zahtjevId: requestId,
      },
    });

    return response.data;
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
      data: {
        paymentId,
        payerId,
      },
    });

    return response.data;
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

    return response.data;
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
    return response.data;
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
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju plaćanja:", err);
    throw err;
  }
}
