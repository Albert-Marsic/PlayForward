import { api } from "../lib/api";

/**
 * Kreiraj PayPal narudžbu za poštarinu
 */
export async function createPayPalOrder(zahtjevId) {
  if (!zahtjevId) {
    throw new Error("ID zahtjeva je obavezan");
  }

  try {
    const response = await api("/placanja/paypal/kreiraj", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ zahtjevId }),
    });
    if (!response.ok) {
      const message = await parseError(response);
      throw new Error(message || "Greška pri kreiranju PayPal narudžbe");
    }
    return await response.json();
  } catch (err) {
    console.error("Greška pri kreiranju PayPal narudžbe:", err);
    throw err;
  }
}

/**
 * Potvrdi PayPal narudžbu za poštarinu
 */
export async function capturePayPalOrder(zahtjevId, orderId) {
  if (!zahtjevId || !orderId) {
    throw new Error("ID zahtjeva i PayPal order ID su obavezni");
  }

  try {
    const response = await api("/placanja/paypal/potvrdi", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ zahtjevId, orderId }),
    });
    if (!response.ok) {
      const message = await parseError(response);
      throw new Error(message || "Greška pri potvrdi PayPal narudžbe");
    }
    return await response.json();
  } catch (err) {
    console.error("Greška pri potvrdi PayPal narudžbe:", err);
    throw err;
  }
}

async function parseError(response) {
  try {
    const data = await response.json();
    if (data && typeof data === "object") {
      return data.message || data.error || JSON.stringify(data);
    }
    return data;
  } catch (err) {
    try {
      const text = await response.text();
      return text || `HTTP ${response.status}`;
    } catch (textErr) {
      return `HTTP ${response.status}`;
    }
  }
}
