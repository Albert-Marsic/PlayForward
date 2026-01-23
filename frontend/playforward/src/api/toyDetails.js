import { api } from "../lib/api";

export async function getToyDetails(toyId) {
  if (!toyId) {
    throw new Error("ID igračke nije prosljeđen");
  }

  try {
    const response = await api(`/igracke/${toyId}`);
    if (!response.ok) throw new Error("Greška pri dohvaćanju detalja igračke");
    return await response.json();
  } catch (err) {
    console.error("Greška pri dohvaćanju detalja igračke:", err);
    throw err;
  }
}
