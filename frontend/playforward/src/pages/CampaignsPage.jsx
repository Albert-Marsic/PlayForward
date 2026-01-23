import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { getCampaigns, calculateCompletionPercentage } from "@/api/campaigns";
import { Calendar } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api";

export default function CampaignsPage() {
  const { user } = useAuth();
  const [campaigns, setCampaigns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [role, setRole] = useState(null);

  useEffect(() => {
    const fetchCampaigns = async () => {
      try {
        setLoading(true);
        const data = await getCampaigns();
        setCampaigns(data || []);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju kampanja");
        console.error(err);
        setCampaigns([]);
      } finally {
        setLoading(false);
      }
    };

    fetchCampaigns();
  }, []);

  useEffect(() => {
    if (!user) {
      setRole(null);
      return;
    }

    let isMounted = true;
    const fetchRole = async () => {
      try {
        const response = await api("/auth/role");
        if (!response.ok) {
          if (isMounted) setRole(null);
          return;
        }
        const data = await response.json();
        if (isMounted) setRole(data?.role ?? null);
      } catch (err) {
        if (isMounted) setRole(null);
      }
    };

    fetchRole();
    return () => {
      isMounted = false;
    };
  }, [user]);

  const isActive = (campaign) => {
    if (campaign.status) return campaign.status === "AKTIVNA";
    const today = new Date();
    const deadline = new Date(campaign.rokTrajanja);
    return deadline >= today;
  };

  if (loading) return <p className="p-6">Učitavanje...</p>;

  const activeCampaigns = campaigns.filter(isActive);
  const completedCampaigns = campaigns.filter(c => !isActive(c));
  const canCreate = role === "RECIPIENT";

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <div className="flex flex-col gap-4 mb-6 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">Dobrotvorne kampanje</h1>
          <p className="text-sm text-gray-600">
            Pregledajte aktivne kampanje ili pokrenite novu akciju.
          </p>
          {user && !canCreate && (
            <p className="text-xs text-gray-500 mt-1">
              Samo udruge mogu kreirati kampanje.
            </p>
          )}
        </div>
        {canCreate && (
          <Button asChild variant="outline">
            <Link to="/kampanje/novo">Kreiraj kampanju</Link>
          </Button>
        )}
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {campaigns.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">Nema dostupnih kampanja</p>
          <Button asChild>
            <Link to="/igracke">Pregledajte dostupne igračke</Link>
          </Button>
        </div>
      ) : (
        <>
          {/* Aktivne kampanje */}
          {activeCampaigns.length > 0 && (
            <div className="mb-12">
              <h2 className="text-xl font-semibold mb-4 text-green-700">Aktivne kampanje</h2>
              <div className="grid md:grid-cols-2 gap-6">
                {activeCampaigns.map(campaign => {
                  const percentage = campaign.popisi?.length
                    ? calculateCompletionPercentage(campaign)
                    : (typeof campaign.postotak === "number" ? campaign.postotak : 0);
                  const daysLeft = Math.ceil((new Date(campaign.rokTrajanja) - new Date()) / (1000 * 60 * 60 * 24));
                  const daysLeftLabel = daysLeft > 0 ? `${daysLeft} dana do kraja` : "Zadnji dan kampanje";
                  const title = campaign.naziv || campaign.napredak || "Kampanja";

                  return (
                    <div key={campaign.id} className="border rounded-lg shadow hover:shadow-lg transition p-4">
                      <div className="flex justify-between items-start mb-3">
                        <h3 className="text-lg font-semibold flex-1">{title}</h3>
                        <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
                          Aktivna
                        </span>
                      </div>

                      {campaign.opis && (
                        <p className="text-sm text-gray-600 mb-3">
                          {campaign.opis}
                        </p>
                      )}

                      {/* Organizator */}
                      <p className="text-sm text-gray-600 mb-3">
                        Organizator: {campaign.primatelj?.email || "Nepoznato"}
                      </p>

                      {/* Rok trajanja */}
                      <p className="text-sm text-gray-600 mb-3 flex items-center gap-2">
                        <Calendar size={16} />
                        {daysLeftLabel}
                      </p>

                      {/* Progress bar */}
                      <div className="mb-3">
                        <div className="flex justify-between text-xs mb-1">
                          <span>Prikupljeno</span>
                          <span className="font-semibold">{percentage}%</span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-2">
                          <div
                            className="bg-green-600 h-2 rounded-full transition-all"
                            style={{ width: `${percentage}%` }}
                          />
                        </div>
                      </div>

                      {/* Info */}
                      <p className="text-xs text-gray-600 mb-4">
                        {campaign.popisi?.length || 0} vrsta igračaka potrebno
                      </p>

                      {/* Gumb */}
                      <Button asChild className="w-full">
                        <Link to={`/kampanja/${campaign.id}`}>
                          Pregledaj kampanju
                        </Link>
                      </Button>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Završene kampanje */}
          {completedCampaigns.length > 0 && (
            <div>
              <h2 className="text-xl font-semibold mb-4 text-gray-700">Završene kampanje</h2>
              <div className="grid md:grid-cols-2 gap-6">
                {completedCampaigns.map(campaign => {
                  const percentage = campaign.popisi?.length
                    ? calculateCompletionPercentage(campaign)
                    : (typeof campaign.postotak === "number" ? campaign.postotak : 0);
                  const title = campaign.naziv || campaign.napredak || "Kampanja";

                  return (
                    <div key={campaign.id} className="border rounded-lg shadow p-4 opacity-75">
                      <div className="flex justify-between items-start mb-3">
                        <h3 className="text-lg font-semibold flex-1">{title}</h3>
                        <span className="bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded">
                          Završena
                        </span>
                      </div>

                      {campaign.opis && (
                        <p className="text-sm text-gray-600 mb-3">
                          {campaign.opis}
                        </p>
                      )}

                      <p className="text-sm text-gray-600 mb-3">
                        Organizator: {campaign.primatelj?.email || "Nepoznato"}
                      </p>

                      <p className="text-sm text-gray-600 mb-3">
                        Završena: {new Date(campaign.rokTrajanja).toLocaleDateString('hr-HR')}
                      </p>

                      <div className="mb-3">
                        <div className="flex justify-between text-xs mb-1">
                          <span>Konačan rezultat</span>
                          <span className="font-semibold">{percentage}%</span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-2">
                          <div
                            className="bg-gray-600 h-2 rounded-full"
                            style={{ width: `${percentage}%` }}
                          />
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
