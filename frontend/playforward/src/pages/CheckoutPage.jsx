import { useCart } from "@/context/CartContext";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";

export default function CheckoutPage() {
  const { cartItems, clearCart } = useCart();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    ime: "",
    prezime: "",
    email: "",
    adresa: "",
    grad: "",
    postanskiBroj: "",
  });

  const totalPrice = cartItems
    .reduce((total, toy) => total + parseFloat(toy.cijena || 0), 0)
    .toFixed(2);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // ovdje će kasnije ići backend poziv
    console.log("Narudžba:", {
      kupac: form,
      igracke: cartItems,
      ukupno: totalPrice,
    });

    alert("Zahtjev za igračke je poslan 🎉");

    clearCart();
    navigate("/");
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
          <div key={toy.idIgracka} className="flex justify-between mb-2">
            <span>{toy.naziv}</span>
            <span>{toy.cijena.toFixed(2)} €</span>
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

        <div className="flex justify-between mt-6">
          <Button variant="outline" asChild>
            <Link to="/kosarica">← Natrag</Link>
          </Button>

          <Button type="submit">
            Potvrdi zahtjev
          </Button>
        </div>
      </form>
    </div>
  );
}