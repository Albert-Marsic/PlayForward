import { useParams, Link } from "react-router-dom";
import fakeData from "@/data/myFakeData";
import { Button } from "@/components/ui/button";
import { useCart } from "@/context/CartContext";

export default function ToyDetails() {
  const { id } = useParams();
  const { addToCart } = useCart();

  const toy = fakeData.find(
    t => String(t.idIgracka) === id
  );

  if (!toy) {
    return (
      <div className="p-6 text-center">
        <h1 className="text-xl font-bold">Igračka nije pronađena 😢</h1>
        <Link to="/igracke" className="underline">
          Povratak na popis
        </Link>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <Link to="/igracke" className="underline text-sm">
        ← Natrag
      </Link>

      <div className="grid md:grid-cols-2 gap-8 mt-4">
        <img
          src={toy.fotografija}
          alt={toy.naziv}
          className="w-full rounded object-cover"
        />

        <div>
          <h1 className="text-3xl font-bold mb-2">{toy.naziv}</h1>
          <p>Kategorija: {toy.kategorija}</p>
          <p>Stanje: {toy.stanje}</p>
          <p>Status: {toy.status}</p>

          <Button className="mt-6" onClick={() => addToCart(toy)}>
            Dodaj u košaricu
          </Button>
        </div>
      </div>
    </div>
  );
}
