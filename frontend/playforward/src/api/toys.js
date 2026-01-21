//import fakeData from "@/data/myFakeData";
import { api } from "../lib/api";

export async function getToys() {
  try {
    const response = await api("/igracke");
    if (!response.ok) throw new Error("Greška pri učitavanju igračaka");
    const data = await response.json();
    return data || [];
  } catch (err) {
    console.error("Greška pri učitavanju igračaka:", err);
    throw err;
  }
}
