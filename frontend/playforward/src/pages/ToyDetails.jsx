import { useParams, Link } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
//import fakeData from "@/data/myFakeData";
import { getToyDetails } from "@/api/toyDetails";

export default function ToyDetails() {
  const { toyId } = useParams();
  const { addToCart } = useCart();
  const [toy, setToy] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchToy = async () => {
      try {
        setLoading(true);
        const data = await getToyDetails(toyId);
        setToy(data);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju igračke");
        console.error(err);
        setToy(null);
      } finally {
        setLoading(false);
      }
    };

    fetchToy();
  }, [toyId]);

  if (loading) return <p className="p-6">Učitavanje...</p>;
  if (error) return <p className="p-6 text-red-500">{error}</p>;
  if (!toy) return <p className="p-6">Nema igračke</p>;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <img src={toy.fotografija} className="w-full max-h-96 object-cover" />
      <h1 className="text-2xl font-bold mt-4">{toy.naziv}</h1>
      <Button className="mt-4" onClick={() => addToCart(toy)}>
        Dodaj u košaricu
      </Button>
      <Link to="/igracke">← Nazad</Link>
    </div>
  );
}
