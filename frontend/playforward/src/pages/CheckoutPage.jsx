import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { createCheckoutRequest } from "@/api/request";
import PayPalButton from "@/components/PayPalButton";
import { useNotification } from "@/context/NotificationContext";
import { DollarSign } from "lucide-react";

export default function CheckoutPage() {
  const { cartItems, clearCart } = useCart();
  const navigate = useNavigate();
  const { addNotification } = useNotification();

  const [form, setForm] = useState({
    ime: "",
    prezime: "",
    email: "",
    adresa: "",
    grad: "",
    postanskiBroj: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("direct"); // direct ili paypal
  const [showPayPal, setShowPayPal] = useState(false);

  const totalPrice = cartItems
    .reduce((total, toy) => total + parseFloat(toy.cijena || 0), 0)
    .toFixed(2);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Provjera da su polja popunjena
    if (!form.ime || !form.prezime || !form.email || !form.adresa || !form.grad || !form.postanskiBroj) {
      setError("Molimo popunite sva polja");
      return;
    }

    if (paymentMethod === "paypal") {
      setShowPayPal(true);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Dohvati samo ID-ove igračaka
      const toyIds = cartItems.map(toy => toy.IDIgracka || toy.id);

      // Pošalji zahtjev na backend
      await createCheckoutRequest(toyIds);

      addNotification("Zahtjev za igračke je poslan 🎉", "success");
      clearCart();
      navigate("/");
    } catch (err) {
      setError("Greška pri slanju zahtjeva: " + err.message);
      console.error(err);
      addNotification("Greška pri slanju zahtjeva", "error");
    } finally {
      setLoading(false);
    }
  };

  const handlePayPalSuccess = async (details) => {
    try {
      setLoading(true);
      
      // Dohvati samo ID-ove igračaka
      const toyIds = cartItems.map(toy => toy.IDIgracka || toy.id);

      // Pošalji zahtjev na backend
      await createCheckoutRequest(toyIds);

      clearCart();
      navigate("/moji-zahtjevi");
    } catch (err) {
      setError("Greška pri slanju zahtjeva: " + err.message);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0) {
    return (
      <div className="p-6 text-center">
        <h1 className="text-xl font-bold">Košarica je prazna 🛒</h1>
        <Link to="/igracke" className="underline text-blue-600">
          Povratak na igračke
        </Link>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Checkout / Kupovina</h1>

      {/* POPIS IGRAČAKA */}
      <div className="mb-6 border rounded p-4">
        <h2 className="font-semibold mb-3">Odabrane igračke</h2>

        {cartItems.map(toy => (
          <div key={toy.IDIgracka || toy.id} className="flex justify-between mb-2">
            <span>{toy.naziv}</span>
            <span>{toy.cijena?.toFixed(2) || "0.00"} €</span>
          </div>
        ))}

        <hr className="my-2" />

        <div className="flex justify-between font-bold">
          <span>Ukupno:</span>
          <span>{totalPrice} €</span>
        </div>
      </div>

      {/* FORMA */}
      <form onSubmit={handleSubmit} className="grid gap-4">
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <div className="grid md:grid-cols-2 gap-4">
          <input
            type="text"
            name="ime"
            placeholder="Ime"
            value={form.ime}
            onChange={handleChange}
            required
            className="border rounded px-3 py-2"
          />
          <input
            type="text"
            name="prezime"
            placeholder="Prezime"
            value={form.prezime}
            onChange={handleChange}
            required
            className="border rounded px-3 py-2"
          />
        </div>

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
          className="border rounded px-3 py-2"
        />

        <input
          type="text"
          name="adresa"
          placeholder="Adresa"
          value={form.adresa}
          onChange={handleChange}
          required
          className="border rounded px-3 py-2"
        />

        <div className="grid md:grid-cols-2 gap-4">
          <input
            type="text"
            name="grad"
            placeholder="Grad"
            value={form.grad}
            onChange={handleChange}
            required
            className="border rounded px-3 py-2"
          />
          <input
            type="text"
            name="postanskiBroj"
            placeholder="Poštanski broj"
            value={form.postanskiBroj}
            onChange={handleChange}
            required
            className="border rounded px-3 py-2"
          />
        </div>

        {/* Metoda plaćanja */}
        <div className="border rounded p-4 my-4">
          <h3 className="font-semibold mb-3">Odaberite metodu plaćanja</h3>
          <div className="flex gap-4">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="radio"
                value="direct"
                checked={paymentMethod === "direct"}
                onChange={(e) => {
                  setPaymentMethod(e.target.value);
                  setShowPayPal(false);
                }}
              />
              <span>Direktan zahtjev (bez plaćanja)</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="radio"
                value="paypal"
                checked={paymentMethod === "paypal"}
                onChange={(e) => setPaymentMethod(e.target.value)}
              />
              <DollarSign size={16} />
              <span>PayPal (dostava je plaćena)</span>
            </label>
          </div>
        </div>

        {/* PayPal gumb */}
        {paymentMethod === "paypal" && showPayPal && (
          <div className="border rounded p-4 bg-blue-50">
            <h3 className="font-semibold mb-3">Plaćanje dostave</h3>
            <p className="text-sm text-gray-600 mb-4">
              Iznos dostave: <strong>{totalPrice} €</strong>
            </p>
            <PayPalButton
              amount={parseFloat(totalPrice)}
              description={`Dostava igračaka - ${cartItems.length} kom`}
              requestId={null}
              onSuccess={handlePayPalSuccess}
              onError={(err) => setError("PayPal greška: " + err.message)}
            />
          </div>
        )}

        <div className="flex justify-between mt-6">
          <Button variant="outline" asChild>
            <Link to="/kosarica">← Natrag</Link>
          </Button>

          <Button type="submit" disabled={loading}>
            {loading ? "Slanje..." : paymentMethod === "paypal" ? "Nastavi na PayPal" : "Potvrdi zahtjev"}
          </Button>
        </div>
      </form>
    </div>
  );
}