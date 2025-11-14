import fakeData from "@/data/myFakeData";
import axios from "axios";

export async function getToys() {
  try {
    const res = await axios.get("http://localhost:5173/api/toys");
    return res.data;
  } catch (err) {
    console.warn("API ne radi — vraćam mock podatke");
    return fakeData;
  }
}
