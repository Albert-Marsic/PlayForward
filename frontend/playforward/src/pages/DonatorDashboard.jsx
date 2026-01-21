import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { getDonatorToys, withdrawToy } from "@/api/dashboard";

export default function DonatorDashboard() {
  const [toys, setToys] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [withdrawing, setWithdrawing] = useState(null);

  useEffect(() => {
    const fetchToys = async () => {
      try {
        setLoading(true);
        const data = await getDonatorToys();
        setToys(data || []);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju vaših donacija");
        console.error(err);
        setToys([]);
      } finally {
        setLoading(false);
      }
    };

    fetchToys();
  }, []);

  const handleWithdraw = async (toyId) => {
    if (!confirm("Jeste li sigurni da želite povući ovaj oglas?")) return;

    try {
      setWithdrawing(toyId);
      await withdrawToy(toyId);
      setToys(toys.filter(toy => (toy.id || toy.idIgracka) !== toyId));
      alert("Oglas je uspješno povučen");
    } catch (err) {
      alert("Greška pri povlačenju oglasa: " + err.message);
    } finally {
      setWithdrawing(null);
    }
  };

  if (loading) return <p className="p-6">Učitavanje...</p>;

  const dostupne = toys.filter(t => t.status === "DOSTUPNO").length;
  const rezervirane = toys.filter(t => t.status === "REZERVIRANO").length;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Moje donacije</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {/* Statistika */}
      <div className="grid md:grid-cols-3 gap-4 mb-6">
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Ukupno donacija</p>
          <p className="text-2xl font-bold">{toys.length}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Dostupne</p>
          <p className="text-2xl font-bold text-green-600">{dostupne}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Rezervirane</p>
          <p className="text-2xl font-bold text-blue-600">{rezervirane}</p>
        </div>
      </div>

      {toys.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">Još niste donirali nijednu igračku</p>
          <Button asChild>
            <Link to="/doniraj">Počnite donirati</Link>
          </Button>
        </div>
      ) : (
        <div className="space-y-4">
          {toys.map(toy => (
            <div key={toy.id || toy.idIgracka} className="border rounded-lg p-4 shadow hover:shadow-lg transition">
              <div className="flex gap-4">
                {/* Slika */}
                <img 
                  src={toy.fotografija} 
                  alt={toy.naziv}
                  className="w-24 h-24 object-cover rounded"
                />

                {/* Podaci */}
                <div className="flex-1">
                  <h2 className="text-lg font-semibold">{toy.naziv}</h2>
                  <p className="text-sm text-gray-600">Kategorija: {toy.kategorija}</p>
                  <p className="text-sm text-gray-600">Stanje: {toy.stanje}</p>
                  
                  {toy.uvjeti && (
                    <p className="text-sm text-gray-600">Uvjeti: {toy.uvjeti}</p>
                  )}

                  <div className="mt-2">
                    <span className={`text-xs font-semibold px-2 py-1 rounded ${
                      toy.status === "DOSTUPNO" 
                        ? "bg-green-100 text-green-800" 
                        : "bg-blue-100 text-blue-800"
                    }`}>
                      {toy.status === "DOSTUPNO" ? "Dostupno" : "Rezervirano"}
                    </span>
                  </div>
                </div>

                {/* Akcije */}
                <div className="flex flex-col gap-2">
                  {toy.status === "DOSTUPNO" && (
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() => handleWithdraw(toy.id || toy.idIgracka)}
                      disabled={withdrawing === (toy.id || toy.idIgracka)}
                    >
                      {withdrawing === (toy.id || toy.idIgracka) ? "Povlačenje..." : "Povuci oglas"}
                    </Button>
                  )}
                  
                  {toy.status === "REZERVIRANO" && (
                    <p className="text-xs text-gray-500 text-center">
                      Igračka je rezervirana
                    </p>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
