import { useParams, useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { submitReview } from "@/api/reviews";
import { Star } from "lucide-react";

export default function ReviewPage() {
  const { requestId } = useParams();
  const navigate = useNavigate();

  const [ocjena, setOcjena] = useState(5);
  const [tekst, setTekst] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // TODO: Trebao bi dohvatiti podatke o zahtjevu (donator, igračka) iz API-ja
  // Za sada ćemo pretpostaviti da su dostupni

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!tekst.trim()) {
      setError("Molimo napišite recenziju");
      return;
    }

    if (tekst.length < 10) {
      setError("Recenzija mora imati najmanje 10 znakova");
      return;
    }

    try {
      setLoading(true);

      // TODO: Trebao bi dohvatiti donator ID iz zahtjeva
      const donatorId = 1; // Placeholder - trebalo bi iz API-ja
      
      await submitReview({
        donatorId,
        ocjena,
        tekst
      });

      alert("Hvala na recenziji! 🙏");
      navigate("/moji-zahtjevi");
    } catch (err) {
      setError(err.message || "Greška pri slanju recenzije");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Ocijeni donatora</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Ocjena sa zvjezdicama */}
        <div>
          <label className="block text-sm font-medium mb-3">
            Koliko ste zadovoljni? *
          </label>
          <div className="flex gap-2">
            {[1, 2, 3, 4, 5].map(star => (
              <button
                key={star}
                type="button"
                onClick={() => setOcjena(star)}
                className="transition transform hover:scale-110"
              >
                <Star
                  size={32}
                  className={`${
                    star <= ocjena
                      ? "fill-yellow-400 text-yellow-400"
                      : "text-gray-300"
                  }`}
                />
              </button>
            ))}
          </div>
          <p className="text-sm text-gray-600 mt-2">
            Vaša ocjena: {ocjena} / 5
          </p>
        </div>

        {/* Tekst recenzije */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Vaša recenzija *
          </label>
          <textarea
            value={tekst}
            onChange={(e) => setTekst(e.target.value)}
            placeholder="Napišite vašu recenziju... (najmanje 10 znakova)"
            maxLength={500}
            rows={6}
            className="w-full border rounded px-3 py-2 resize-none"
            required
          />
          <p className="text-xs text-gray-500 mt-1">
            {tekst.length}/500 znakova
          </p>
        </div>

        {/* Dugmići */}
        <div className="flex gap-3 justify-end">
          <Button variant="outline" asChild>
            <Link to="/moji-zahtjevi">Otkaži</Link>
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? "Slanje..." : "Pošalji recenziju"}
          </Button>
        </div>
      </form>

      {/* Info */}
      <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded text-sm text-blue-900">
        <p>
          <strong>Savjet:</strong> Napišite iskrenu recenziju o vašem iskustvu s donatorom. 
          To pomaže drugim korisnicima da donesu bolju odluku.
        </p>
      </div>
    </div>
  );
}
