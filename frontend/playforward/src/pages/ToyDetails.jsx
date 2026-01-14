import { useParams, Link } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import fakeData from "@/data/myFakeData";

export default function ToyDetails() {
  const { id } = useParams();
  const { addToCart } = useCart();

  const toy = fakeData.find(t => String(t.idIgracka) === id);

  if (!toy) return <p>Nema igračke</p>;

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
