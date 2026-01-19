import axios from "axios";
import { API_BASE_URL } from "../lib/config";
import { api } from "../lib/api";

/**
 * Dohvati sve igračke donatora (s JWT tokenima)
 */
export async function getDonatorToys() {
  try {
    const response = await api("/igracke");
    if (!response.ok) throw new Error("Greška pri dohvaćanju donacija");
    return await response.json();
  } catch (err) {
    console.error("Greška pri dohvaćanju mojih donacija:", err);
    throw err;
  }
}

/**
 * Pobvuci oglas (obriši igračku)
 */
export async function withdrawToy(toyId) {
  if (!toyId) throw new Error("ID igračke je obavezan");
  
  try {
    const response = await api(`/igracke/${toyId}`, { method: "DELETE" });
    if (!response.ok) throw new Error("Greška pri povlačenju oglasa");
    return await response.json();
  } catch (err) {
    console.error("Greška pri povlačenju oglasa:", err);
    throw err;
  }
}

/**
 * Dohvati sve zahtjeve korisnika (primatelja)
 * Pretpostavlja da backend ima endpoint koji vraća zahtjeve
 */
export async function getMyRequests() {
  try {
    const response = await api("/zahtjevi"); // ili /requests
    if (!response.ok) throw new Error("Greška pri dohvaćanju zahtjeva");
    return await response.json();
  } catch (err) {
    console.error("Greška pri dohvaćanju mojih zahtjeva:", err);
    throw err;
  }
}

/**
 * Odustani od zahtjeva (obriši zahtjev)
 */
export async function withdrawRequest(requestId) {
  if (!requestId) throw new Error("ID zahtjeva je obavezan");
  
  try {
    const response = await api(`/zahtjevi/${requestId}`, { method: "DELETE" });
    if (!response.ok) throw new Error("Greška pri odustajanju");
    return await response.json();
  } catch (err) {
    console.error("Greška pri odustajanju od zahtjeva:", err);
    throw err;
  }
}

/**
 * Potvrdi preuzimanje zahtjeva (status -> COMPLETED)
 */
export async function completeRequest(requestId) {
  if (!requestId) throw new Error("ID zahtjeva je obavezan");

  try {
    const response = await api(`/zahtjevi/${requestId}/preuzeto`, { method: "POST" });
    if (!response.ok) throw new Error("Greška pri potvrdi preuzimanja");
    return await response.json();
  } catch (err) {
    console.error("Greška pri potvrdi preuzimanja:", err);
    throw err;
  }
}
