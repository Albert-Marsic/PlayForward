import { useParams, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { getCampaignDetails, calculateCompletionPercentage, requestToyFromCampaign } from "@/api/campaigns";
import { Calendar, CheckCircle, Clock } from "lucide-react";

export default function CampaignDetailsPage() {
  const { campaignId } = useParams();

  const [campaign, setCampaign] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [requesting, setRequesting] = useState(null);

  useEffect(() => {
    const fetchCampaign = async () => {
      try {
        setLoading(true);
        const data = await getCampaignDetails(campaignId);
        setCampaign(data);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju kampanje");
        console.error(err);
        setCampaign(null);
      } finally {
        setLoading(false);
      }
    };

    fetchCampaign();
  }, [campaignId]);

  const handleRequestToy = async (toyId) => {
    try {
      setRequesting(toyId);
      const updated = await requestToyFromCampaign(campaignId, toyId);
      setCampaign(prev => {
        if (!prev?.popisi) return prev;
        const updatedPopisi = prev.popisi.map(popis => {
          const naziv = popis.nazivIgracke || popis.id?.nazivIgracke;
          if (naziv === updated?.nazivIgracke) {
            return { ...popis, status: updated.status };
          }
          return popis;
        });
        return { ...prev, popisi: updatedPopisi };
      });
      alert("Hvala na donaciji! ✅");
    } catch (err) {
      alert("Greška pri slanju zahtjeva: " + err.message);
    } finally {
      setRequesting(null);
    }
  };

  if (loading) return <p className="p-6">Učitavanje...</p>;
  if (error || !campaign) {
    return (
      <div className="p-6 text-center">
        <p className="text-red-600 mb-4">{error || "Kampanja nije pronađena"}</p>
        <Button asChild>
          <Link to="/kampanje">Nazad na kampanje</Link>
        </Button>
      </div>
    );
  }

  const percentage = campaign.popisi?.length
    ? calculateCompletionPercentage(campaign)
    : (typeof campaign.postotak === "number" ? campaign.postotak : 0);
  const daysLeft = Math.ceil((new Date(campaign.rokTrajanja) - new Date()) / (1000 * 60 * 60 * 24));
  const isActive = campaign.status ? campaign.status === "AKTIVNA" : daysLeft >= 0;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <Link to="/kampanje" className="text-blue-600 hover:underline mb-4 inline-block">
          ← Nazad na kampanje
        </Link>

        <div className="flex justify-between items-start mb-4">
          <h1 className="text-3xl font-bold">{campaign.naziv || campaign.napredak || "Kampanja"}</h1>
          <span className={`text-xs px-3 py-1 rounded font-semibold ${
            isActive ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-800"
          }`}>
            {isActive ? "Aktivna" : "Završena"}
          </span>
        </div>

        {campaign.opis && (
          <p className="text-gray-700 mb-4">{campaign.opis}</p>
        )}

        <p className="text-gray-600 mb-4">
          Organizator: <strong>{campaign.primatelj?.email || "Nepoznato"}</strong>
        </p>
      </div>

      {/* Info */}
      <div className="grid md:grid-cols-2 gap-4 mb-6">
        <div className="border rounded p-4">
          <div className="flex items-center gap-2 text-sm text-gray-600 mb-2">
            <Calendar size={16} />
            <span>Rok trajanja</span>
          </div>
          <p className="font-semibold">
            {new Date(campaign.rokTrajanja).toLocaleDateString('hr-HR')}
          </p>
          {isActive && (
            <p className="text-sm text-green-600 mt-1">
              {daysLeft} dana preostalo
            </p>
          )}
        </div>

        <div className="border rounded p-4">
          <div className="flex items-center gap-2 text-sm text-gray-600 mb-2">
            <Clock size={16} />
            <span>Prikupljeno</span>
          </div>
          <p className="font-semibold text-lg">{percentage}%</p>
          <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
            <div
              className="bg-green-600 h-2 rounded-full transition-all"
              style={{ width: `${percentage}%` }}
            />
          </div>
        </div>
      </div>

      {/* Lista igračaka */}
      <div className="border rounded-lg shadow p-6">
        <h2 className="text-xl font-semibold mb-4">Potrebne igračke</h2>

        {campaign.popisi && campaign.popisi.length > 0 ? (
          <div className="space-y-3">
            {campaign.popisi.map((popis, idx) => {
              const itemKey = popis.nazivIgracke || popis.id?.nazivIgracke;
              const displayName = itemKey
                || popis.igracka?.naziv
                || `Igračka #${idx + 1}`;
              return (
                <div key={`${campaign.id}-${itemKey || idx}`} className="flex items-center justify-between border-b pb-3">
                  <div className="flex-1">
                    <p className="font-medium">
                      {displayName}
                    </p>
                    <p className="text-sm text-gray-600">
                      Potrebno: {popis.kolicina} kom
                    </p>
                    <div className="flex items-center gap-2 mt-1">
                      {popis.status === "DONIRANO" && (
                        <span className="flex items-center gap-1 text-xs text-green-600">
                          <CheckCircle size={14} />
                          Prikupljeno
                        </span>
                      )}
                    </div>
                  </div>

                  {isActive && popis.status !== "DONIRANO" && itemKey && (
                    <Button
                      size="sm"
                      onClick={() => handleRequestToy(itemKey)}
                      disabled={requesting === itemKey}
                    >
                      {requesting === itemKey ? "Slanje..." : "Želim donirati"}
                    </Button>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <p className="text-gray-600">Nema specificiranih igračaka u kampanju</p>
        )}
      </div>

      {/* Info text */}
      <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded text-sm text-blue-900">
        <p>
          <strong>Kako donirati:</strong> Odaberite igračku koju želite donirati, 
          i sustav će vas povezati sa organizatorom kampanje.
        </p>
      </div>
    </div>
  );
}
