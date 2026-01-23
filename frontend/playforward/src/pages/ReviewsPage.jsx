import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Star } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { getReviewsForDonator, getReviewsForPrimatelj } from "@/api/reviews";
import { Button } from "@/components/ui/button";

function StarRating({ value }) {
  const rating = Math.max(0, Math.min(5, Number(value) || 0));

  return (
    <div className="flex items-center gap-2">
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            size={18}
            className={
              star <= rating
                ? "fill-yellow-400 text-yellow-400"
                : "text-gray-300"
            }
          />
        ))}
      </div>
      <span className="text-sm text-gray-600">{rating}/5</span>
    </div>
  );
}

export default function ReviewsPage() {
  const { user, loading } = useAuth();
  const [reviews, setReviews] = useState([]);
  const [fetching, setFetching] = useState(true);
  const [error, setError] = useState(null);

  const role = user?.role || user?.uloga;
  const isDonator = role === "DONATOR";
  const isPrimatelj = role === "RECIPIENT" || role === "PRIMATELJ";
  const canLoad = isDonator || isPrimatelj;

  useEffect(() => {
    if (loading) return;

    if (!user) {
      setReviews([]);
      setError(null);
      setFetching(false);
      return;
    }

    if (!canLoad) {
      setReviews([]);
      setError("Odaberite ulogu kako biste vidjeli recenzije.");
      setFetching(false);
      return;
    }

    const fetchReviews = async () => {
      try {
        setFetching(true);
        const data = isDonator
          ? await getReviewsForDonator()
          : await getReviewsForPrimatelj();
        setReviews(data || []);
        setError(null);
      } catch (err) {
        setError(err.message || "Greška pri dohvaćanju recenzija");
        setReviews([]);
      } finally {
        setFetching(false);
      }
    };

    fetchReviews();
  }, [loading, user, canLoad, isDonator]);

  if (loading) {
    return <p className="p-6">Učitavanje...</p>;
  }

  if (!user) {
    return (
      <div className="p-6 max-w-2xl mx-auto text-center space-y-4">
        <h1 className="text-2xl font-bold">Moje recenzije</h1>
        <p className="text-gray-600">Za pregled recenzija morate biti prijavljeni.</p>
        <Button asChild>
          <Link to="/prijava">Prijava</Link>
        </Button>
      </div>
    );
  }

  const title = isDonator
    ? "Recenzije koje sam primio/la"
    : isPrimatelj
      ? "Recenzije koje sam dao/la"
      : "Moje recenzije";
  const counterpartyLabel = isDonator ? "Primatelj" : "Donator";
  const dashboardLink = isDonator
    ? "/moje-donacije"
    : isPrimatelj
      ? "/moji-zahtjevi"
      : "/dashboard";

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <div className="flex flex-col gap-3 mb-6 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold">{title}</h1>
        <Button variant="outline" asChild>
          <Link to={dashboardLink}>Natrag na dashboard</Link>
        </Button>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {fetching ? (
        <p>Učitavanje recenzija...</p>
      ) : reviews.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">Još nema recenzija.</p>
          <Button asChild>
            <Link to={dashboardLink}>Povratak na dashboard</Link>
          </Button>
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.map((review) => {
            const counterparty = isDonator
              ? review.primateljEmail
              : review.donatorEmail;
            return (
              <div key={review.id} className="border rounded-lg p-4 shadow-sm">
                <div className="flex flex-col gap-3">
                  <div className="flex flex-col gap-1">
                    <StarRating value={review.ocjena} />
                    <p className="text-sm text-gray-600">
                      {counterpartyLabel}: {counterparty || "Nepoznato"}
                    </p>
                    {review.igrackaNaziv && (
                      <p className="text-sm text-gray-600">
                        Igračka: {review.igrackaNaziv}
                      </p>
                    )}
                    {review.zahtjevId && (
                      <p className="text-sm text-gray-500">
                        Zahtjev #{review.zahtjevId}
                      </p>
                    )}
                  </div>
                  <p className="text-gray-800 whitespace-pre-wrap">
                    {review.tekst}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
