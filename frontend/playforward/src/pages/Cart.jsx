import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { Heart } from "lucide-react";

export default function Kosarica() {
  const { cartItems, removeFromCart, clearCart } = useCart();

  if (cartItems.length === 0) {
    return (
      <div className="p-6 text-center">
        <Heart className="w-16 h-16 mx-auto mb-4 text-gray-300" />
        <h1 className="text-xl font-bold mb-2">Nemate odabranih igračaka</h1>
        <p className="text-gray-600 mb-4">Pregledajte dostupne igračke i odaberite one koje želite zatražiti.</p>
        <Button asChild>
          <Link to="/igracke">Pregledaj igračke</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-2">Odabrane igračke</h1>
      <p className="text-gray-600 mb-6">Pregledajte igračke koje ste odabrali i pošaljite zahtjev za donaciju.</p>

      {cartItems.map(toy => (
        <div key={toy.id || toy.idIgracka} className="flex gap-4 border rounded-lg p-4 mb-4">
          <img src={toy.fotografija} alt={toy.naziv} className="w-24 h-24 object-cover rounded" />
          <div className="flex-1">
            <h2 className="font-semibold">{toy.naziv}</h2>
            <p className="text-sm text-gray-600">Kategorija: {toy.kategorija}</p>
            <p className="text-sm text-gray-600">Stanje: {toy.stanje}</p>
          </div>
          <Button variant="outline" onClick={() => removeFromCart(toy.id || toy.idIgracka)}>
            Ukloni
          </Button>
        </div>
      ))}

      <div className="flex justify-between mt-6">
        <Button variant="outline" onClick={clearCart}>
          Očisti sve
        </Button>
        <Button asChild className="bg-green-600 hover:bg-green-700">
          <Link to="/kupovina">Pošalji zahtjev za donaciju</Link>
        </Button>
      </div>
    </div>
  );
}
