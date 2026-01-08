import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

export default function Kosarica() {
  const { cartItems, removeFromCart, clearCart } = useCart();

  if (cartItems.length === 0) {
    return (
      <div className="p-6 text-center">
        <h1 className="text-xl font-bold">Košarica je prazna 🛒</h1>
        <Link to="/igracke" className="underline">
          Povratak na igračke
        </Link>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Košarica</h1>

      {cartItems.map(toy => (
        <div key={toy.idIgracka} className="flex gap-4 border p-4 mb-4">
          <img src={toy.fotografija} className="w-24 h-24 object-cover" />
          <div className="flex-1">
            <h2 className="font-semibold">{toy.naziv}</h2>
            <p>{toy.kategorija}</p>
          </div>
          <Button variant="outline" onClick={() => removeFromCart(toy.idIgracka)}>
            Ukloni
          </Button>
        </div>
      ))}

      <div className="flex justify-between mt-6">
        <Button variant="outline" onClick={clearCart}>
          Očisti
        </Button>
        <Button>Zatraži igračke</Button>
      </div>
    </div>
  );
}
