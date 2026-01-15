//import fakeData from "@/data/myFakeData";
import axios from "axios";
import { API_BASE_URL } from "../lib/config";

export async function getToys() {
  try {
    const res = await axios.get(`http://${API_BASE_URL}/api/igracke`);
    return res.data || [];
  } catch (err) {
    console.error("Greška pri učitavanju igračaka:", err);
    throw err;
  }
}
