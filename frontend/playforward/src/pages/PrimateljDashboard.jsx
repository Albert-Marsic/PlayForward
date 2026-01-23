import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { getMyRequests, withdrawRequest } from "@/api/dashboard";
import PayPalButton from "@/components/PayPalButton";

export default function PrimateljDashboard() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [withdrawing, setWithdrawing] = useState(null);

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        setLoading(true);
        const data = await getMyRequests();
        setRequests(data || []);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju vaših zahtjeva");
        console.error(err);
        setRequests([]);
      } finally {
        setLoading(false);
      }
    };

    fetchRequests();
  }, []);

  const handleWithdraw = async (requestId) => {
    if (!confirm("Jeste li sigurni da želite odustati od ovog zahtjeva?")) return;

    try {
      setWithdrawing(requestId);
      await withdrawRequest(requestId);
      setRequests(requests.filter(req => req.id !== requestId));
      alert("Odustali ste od zahtjeva");
    } catch (err) {
      alert("Greška pri odustajanju: " + err.message);
    } finally {
      setWithdrawing(null);
    }
  };

  const handlePostagePaid = (updatedRequest) => {
    if (!updatedRequest || !updatedRequest.id) return;
    setRequests(prev => prev.map(req => (req.id === updatedRequest.id ? updatedRequest : req)));
  };

  if (loading) return <p className="p-6">Učitavanje...</p>;

  const pending = requests.filter(r => r.status === "PENDING").length;
  const postagePending = requests.filter(r => r.status === "POSTAGE_PENDING" || r.status === "APPROVED").length;
  const postagePaid = requests.filter(r => r.status === "POSTAGE_PAID").length;
  const pickedUp = requests.filter(r => r.status === "PICKED_UP" || r.status === "COMPLETED").length;

  // Mapiranje statusa na hrvatsku
  const getStatusLabel = (status) => {
    switch(status) {
      case "PENDING": return "U čekanju";
      case "APPROVED": return "Odobren - čeka poštarinu";
      case "POSTAGE_PENDING": return "Čeka plaćanje poštarine";
      case "POSTAGE_PAID": return "Poštarina plaćena";
      case "PICKED_UP": return "Preuzeto";
      case "COMPLETED": return "Preuzeto";
      case "REJECTED": return "Odbijen";
      case "WITHDRAWN": return "Odustano";
      default: return status;
    }
  };

  const getStatusColor = (status) => {
    switch(status) {
      case "PENDING": return "bg-yellow-100 text-yellow-800";
      case "APPROVED": return "bg-orange-100 text-orange-800";
      case "POSTAGE_PENDING": return "bg-orange-100 text-orange-800";
      case "POSTAGE_PAID": return "bg-indigo-100 text-indigo-800";
      case "PICKED_UP": return "bg-green-100 text-green-800";
      case "COMPLETED": return "bg-green-100 text-green-800";
      case "REJECTED": return "bg-red-100 text-red-800";
      case "WITHDRAWN": return "bg-gray-100 text-gray-800";
      default: return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="flex flex-col gap-4 mb-6 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold">Moji zahtjevi</h1>
        <Button asChild variant="outline">
          <Link to="/igracke">Novi zahtjev</Link>
        </Button>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {/* Statistika */}
      <div className="grid md:grid-cols-5 gap-4 mb-6">
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Ukupno zahtjeva</p>
          <p className="text-2xl font-bold">{requests.length}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">U čekanju</p>
          <p className="text-2xl font-bold text-yellow-600">{pending}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Čeka poštarinu</p>
          <p className="text-2xl font-bold text-orange-600">{postagePending}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Poštarina plaćena</p>
          <p className="text-2xl font-bold text-indigo-600">{postagePaid}</p>
        </div>
        <div className="border rounded p-4">
          <p className="text-gray-600 text-sm">Preuzeto</p>
          <p className="text-2xl font-bold text-green-600">{pickedUp}</p>
        </div>
      </div>

      {requests.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">Niste poslali nijedan zahtjev</p>
          <Button asChild>
            <Link to="/igracke">Pregledajte dostupne igračke</Link>
          </Button>
        </div>
      ) : (
        <div className="space-y-4">
          {requests.map(request => (
            <div key={request.id} className="border rounded-lg p-4 shadow hover:shadow-lg transition">
              <div className="flex gap-4">
                {/* Slika igračke */}
                {request.igracka?.fotografija && (
                  <img 
                    src={request.igracka.fotografija} 
                    alt={request.igracka.naziv}
                    className="w-24 h-24 object-cover rounded"
                  />
                )}

                {/* Podaci */}
                <div className="flex-1">
                  <h2 className="text-lg font-semibold">{request.igracka?.naziv || "Igračka"}</h2>
                  <p className="text-sm text-gray-600">
                    Donator: {request.donator?.korisnik?.email || request.igracka?.donator?.korisnik?.email || "Nepoznato"}
                  </p>
                  <p className="text-sm text-gray-600">
                    Datumi zahtjeva: {new Date(request.datumZahtjeva).toLocaleDateString('hr-HR')}
                  </p>

                  {request.napomena && (
                    <p className="text-sm text-gray-600">Napomena: {request.napomena}</p>
                  )}

                  <div className="mt-2">
                    <span className={`text-xs font-semibold px-2 py-1 rounded ${getStatusColor(request.status)}`}>
                      {getStatusLabel(request.status)}
                    </span>
                  </div>
                </div>

                {/* Akcije */}
                <div className="flex flex-col gap-2">
                  {request.status === "PENDING" && (
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() => handleWithdraw(request.id)}
                      disabled={withdrawing === request.id}
                    >
                      {withdrawing === request.id ? "Odustajanje..." : "Odustani"}
                    </Button>
                  )}

                  {(request.status === "PICKED_UP" || request.status === "COMPLETED") && (
                    <Button variant="outline" size="sm" asChild>
                      <Link to={`/recenzija/${request.id}`}>Ocijeni donatora</Link>
                    </Button>
                  )}

                  {(request.status === "POSTAGE_PENDING" || request.status === "APPROVED") && (
                    <PayPalButton
                      requestId={request.id}
                      onSuccess={handlePostagePaid}
                      onError={(err) => alert("Greška pri plaćanju: " + err.message)}
                    />
                  )}

                  {(request.status === "PENDING"
                    || request.status === "APPROVED"
                    || request.status === "POSTAGE_PENDING"
                    || request.status === "POSTAGE_PAID") && (
                    <p className="text-xs text-gray-500 text-center">
                      {request.status === "PENDING"
                        ? "Čeka se odgovor donatora"
                        : request.status === "POSTAGE_PENDING" || request.status === "APPROVED"
                          ? "Odobren - potrebno je platiti poštarinu"
                          : request.status === "POSTAGE_PAID"
                            ? "Poštarina je plaćena - čekajte potvrdu donatora"
                            : "Odobren"}
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
