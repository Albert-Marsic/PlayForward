import { useParams, Link, useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
//import fakeData from "@/data/myFakeData";
import { getToyDetails } from "@/api/toyDetails";
import { api } from "@/lib/api";

export default function ToyDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const { user } = useAuth();
  const [toy, setToy] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [submittingRequest, setSubmittingRequest] = useState(false);
  const [requestNote, setRequestNote] = useState("");

  useEffect(() => {
    const fetchToy = async () => {
      try {
        setLoading(true);
        const data = await getToyDetails(id);
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
  }, [id]);

  const handleSubmitRequest = async () => {
    if (!user) {
      navigate("/prijava");
      return;
    }

    if (user.role !== "RECIPIENT") {
      setError("Samo primatelji mogu slati zahtjeve za igračke.");
      return;
    }

    try {
      setSubmittingRequest(true);
      const response = await api("/zahtjevi", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          igrackaId: parseInt(id),
          napomena: requestNote || null,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Greška pri slanju zahtjeva");
      }

      setError(null);
      setRequestNote("");
      alert("Zahtjev je uspješno poslан!");
      // Refresh toy details
      const updatedToy = await getToyDetails(id);
      setToy(updatedToy);
    } catch (err) {
      setError(err.message || "Greška pri slanju zahtjeva");
      console.error(err);
    } finally {
      setSubmittingRequest(false);
    }
  };

  if (loading) return <p className="p-6 text-center">Učitavanje...</p>;
  if (error) return <p className="p-6 text-red-500 text-center">{error}</p>;
  if (!toy) return <p className="p-6 text-center">Nema igračke</p>;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Slika */}
      <div className="mb-6">
        <img 
          src={toy.fotografija} 
          alt={toy.naziv}
          className="w-full max-h-96 object-cover rounded-lg shadow-md" 
        />
      </div>

      {/* Osnovne informacije */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-2">{toy.naziv}</h1>
        
        <div className="flex gap-4 mb-4">
          <span className={`px-3 py-1 rounded text-sm font-semibold ${
            toy.status === "dostupno" ? "bg-green-100 text-green-800" : 
            toy.status === "rezervirano" ? "bg-yellow-100 text-yellow-800" : 
            "bg-gray-100 text-gray-800"
          }`}>
            {toy.status}
          </span>
          
          <span className="px-3 py-1 rounded text-sm font-semibold bg-blue-100 text-blue-800">
            {toy.kategorija}
          </span>
        </div>
      </div>

      {/* Detalji */}
      <div className="grid md:grid-cols-2 gap-6 mb-6">
        <div className="border rounded-lg p-4 shadow">
          <h2 className="text-lg font-semibold mb-4">Informacije</h2>
          
          <div className="space-y-3">
            <div>
              <p className="text-sm text-gray-600">Stanje</p>
              <p className="font-medium">{toy.stanje}</p>
            </div>
            
            <div>
              <p className="text-sm text-gray-600">Kategorija</p>
              <p className="font-medium">{toy.kategorija}</p>
            </div>
            
            <div>
              <p className="text-sm text-gray-600">Godinu proizvodnje</p>
              <p className="font-medium">{toy.godinaProizvodnje || "Nepoznato"}</p>
            </div>

            {toy.donator && (
              <div>
                <p className="text-sm text-gray-600">Darivač</p>
                <p className="font-medium">{toy.donator.email}</p>
              </div>
            )}
          </div>
        </div>

        {/* Opis */}
        <div className="border rounded-lg p-4 shadow">
          <h2 className="text-lg font-semibold mb-4">Opis</h2>
          <p className="text-gray-700">
            {toy.opis || "Nema dostupnog opisa za ovu igračku."}
          </p>
        </div>
      </div>

      {/* Akcije */}
      <div className="flex gap-4 mb-4">
        {user?.role === "RECIPIENT" ? (
          <>
            <Button 
              className="flex-1 bg-green-600 hover:bg-green-700"
              onClick={handleSubmitRequest}
              disabled={toy.status === "rezervirano" || submittingRequest}
            >
              {submittingRequest ? "Slanje..." : (toy.status === "rezervirano" ? "Rezervirano" : "Zatraži igračku")}
            </Button>
            <Button variant="outline" asChild className="flex-1">
              <Link to="/igracke">← Nazad na igračke</Link>
            </Button>
          </>
        ) : (
          <>
            <Button 
              className="flex-1 bg-green-600 hover:bg-green-700"
              onClick={() => addToCart(toy)}
              disabled={toy.status === "rezervirano"}
            >
              {toy.status === "rezervirano" ? "Rezervirano" : "Dodaj u košaricu"}
            </Button>
            <Button variant="outline" asChild className="flex-1">
              <Link to="/igracke">← Nazad na igračke</Link>
            </Button>
          </>
        )}
      </div>

      {/* Request Note Input (za RECIPIENT) */}
      {user?.role === "RECIPIENT" && toy.status !== "rezervirano" && (
        <div className="mb-4 p-4 border rounded-lg bg-gray-50">
          <label className="block text-sm font-medium mb-2">Dodatna napomena (opciono)</label>
          <textarea
            className="w-full p-2 border rounded"
            rows="3"
            placeholder="Npr. vremenske preferencije za preuzimanje..."
            value={requestNote}
            onChange={(e) => setRequestNote(e.target.value)}
          />
        </div>
      )}

      {/* Info */}
      {toy.status === "rezervirano" && (
        <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded">
          Ova igračka je već rezervirana. Provjerite druge dostupne igračke.
        </div>
      )}
    </div>
  );
}
