import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { getCampaigns, calculateCompletionPercentage } from "@/api/campaigns";
import { Calendar } from "lucide-react";

export default function CampaignsPage() {
  const [campaigns, setCampaigns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

  const isActive = (campaign) => {
    const today = new Date();
    const deadline = new Date(campaign.rokTrajanja);
    return deadline > today;
  };

  if (loading) return <p className="p-6">Učitavanje...</p>;

  const activeCampaigns = campaigns.filter(isActive);
  const completedCampaigns = campaigns.filter(c => !isActive(c));

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Dobrotvorne kampanje</h1>

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
                  const percentage = calculateCompletionPercentage(campaign);
                  const daysLeft = Math.ceil((new Date(campaign.rokTrajanja) - new Date()) / (1000 * 60 * 60 * 24));

                  return (
                    <div key={campaign.id} className="border rounded-lg shadow hover:shadow-lg transition p-4">
                      <div className="flex justify-between items-start mb-3">
                        <h3 className="text-lg font-semibold flex-1">{campaign.napredak}</h3>
                        <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
                          Aktivna
                        </span>
                      </div>

                      {/* Organizator */}
                      <p className="text-sm text-gray-600 mb-3">
                        Organizator: {campaign.primatelj?.email || "Nepoznato"}
                      </p>

                      {/* Rok trajanja */}
                      <p className="text-sm text-gray-600 mb-3 flex items-center gap-2">
                        <Calendar size={16} />
                        {daysLeft > 0 ? `${daysLeft} dana do kraja` : "Kampanja je završena"}
                      </p>

                      {/* Progress bar */}
                      <div className="mb-3">
                        <div className="flex justify-between text-xs mb-1">
                          <span>Napredak</span>
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
                  const percentage = calculateCompletionPercentage(campaign);

                  return (
                    <div key={campaign.id} className="border rounded-lg shadow p-4 opacity-75">
                      <div className="flex justify-between items-start mb-3">
                        <h3 className="text-lg font-semibold flex-1">{campaign.napredak}</h3>
                        <span className="bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded">
                          Završena
                        </span>
                      </div>

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
