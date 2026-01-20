import axios from "axios";
import { API_BASE_URL } from "../lib/config";

export async function getToyDetails(toyId) {
  if (!toyId) {
    throw new Error("ID igračke nije prosljeđen");
  }

  try {
    const res = await axios.get(`http://${API_BASE_URL}/api/igracke/${toyId}`);
    return res.data;
  } catch (err) {
    console.error("Greška pri dohvaćanju detalja igračke:", err);
    throw err;
  }
}
