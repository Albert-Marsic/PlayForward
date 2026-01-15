import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import {
  getAllUsers,
  getAllDonations,
  getAllCampaigns,
  getPlatformStats,
  deleteUser,
  deleteDonation,
  deleteCampaign,
} from "@/api/admin";
import { Button } from "@/components/ui/button";
import { Users, Gift, Target, TrendingUp, Trash2, X } from "lucide-react";

export default function AdminDashboard() {
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [donations, setDonations] = useState([]);
  const [campaigns, setCampaigns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState("stats");
  const [deleting, setDeleting] = useState(null);

  // Provera da li je korisnik admin
  const isAdmin = user?.role === "ADMIN" || user?.uloga === "ADMIN";

  useEffect(() => {
    if (!authLoading && !user) {
      navigate("/prijava");
      return;
    }

    if (!isAdmin) {
      navigate("/");
      return;
    }

    const fetchData = async () => {
      try {
        setLoading(true);
        const [statsData, usersData, donationsData, campaignsData] = await Promise.all([
          getPlatformStats(),
          getAllUsers(),
          getAllDonations(),
          getAllCampaigns(),
        ]);

        setStats(statsData);
        setUsers(usersData);
        setDonations(donationsData);
        setCampaigns(campaignsData);
        setError(null);
      } catch (err) {
        console.error("Greška pri učitavanju admin podataka:", err);
        setError("Nije moguće učitati admin panel");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user, authLoading, isAdmin, navigate]);

  const handleDeleteUser = async (userId) => {
    if (!window.confirm("Sigurno želite obrisati korisnika?")) return;

    try {
      setDeleting(userId);
      await deleteUser(userId);
      setUsers(users.filter((u) => u.id !== userId));
      alert("Korisnik je obrisan");
    } catch (err) {
      alert("Greška pri brisanju korisnika: " + err.message);
    } finally {
      setDeleting(null);
    }
  };

  const handleDeleteDonation = async (donationId) => {
    if (!window.confirm("Sigurno želite obrisati donaciju?")) return;

    try {
      setDeleting(donationId);
      await deleteDonation(donationId);
      setDonations(donations.filter((d) => d.id !== donationId));
      alert("Donacija je obrisana");
    } catch (err) {
      alert("Greška pri brisanju donacije: " + err.message);
    } finally {
      setDeleting(null);
    }
  };

  const handleDeleteCampaign = async (campaignId) => {
    if (!window.confirm("Sigurno želite obrisati kampanju?")) return;

    try {
      setDeleting(campaignId);
      await deleteCampaign(campaignId);
      setCampaigns(campaigns.filter((c) => c.id !== campaignId));
      alert("Kampanja je obrisana");
    } catch (err) {
      alert("Greška pri brisanju kampanje: " + err.message);
    } finally {
      setDeleting(null);
    }
  };

  if (authLoading || loading) {
    return <div className="p-6 text-center">Učitavanje...</div>;
  }

  if (!isAdmin) {
    return (
      <div className="p-6 text-center">
        <p className="text-red-600 mb-4">Nemate pristup admin panelu</p>
        <Button onClick={() => navigate("/")}>Nazad</Button>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-bold">Admin Panel</h1>
        <Button variant="outline" onClick={() => navigate("/")}>
          Nazad
        </Button>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {/* Tabs */}
      <div className="flex gap-4 mb-6 border-b">
        {["stats", "users", "donations", "campaigns"].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`pb-2 px-4 font-medium transition ${
              activeTab === tab
                ? "border-b-2 border-blue-600 text-blue-600"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            {tab === "stats" && "Statistika"}
            {tab === "users" && "Korisnici"}
            {tab === "donations" && "Donacije"}
            {tab === "campaigns" && "Kampanje"}
          </button>
        ))}
      </div>

      {/* Statistika */}
      {activeTab === "stats" && stats && (
        <div className="grid md:grid-cols-4 gap-4">
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-lg p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-600 text-sm font-medium">Ukupno korisnika</p>
                <p className="text-3xl font-bold text-blue-900">
                  {stats.ukupnoKorisnika || 0}
                </p>
              </div>
              <Users size={40} className="text-blue-300" />
            </div>
          </div>

          <div className="bg-gradient-to-br from-green-50 to-green-100 border border-green-200 rounded-lg p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-600 text-sm font-medium">Aktivnih donacija</p>
                <p className="text-3xl font-bold text-green-900">
                  {stats.aktivnihDonacija || 0}
                </p>
              </div>
              <Gift size={40} className="text-green-300" />
            </div>
          </div>

          <div className="bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-200 rounded-lg p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-purple-600 text-sm font-medium">Aktivnih kampanja</p>
                <p className="text-3xl font-bold text-purple-900">
                  {stats.aktivnihKampanja || 0}
                </p>
              </div>
              <Target size={40} className="text-purple-300" />
            </div>
          </div>

          <div className="bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-200 rounded-lg p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-orange-600 text-sm font-medium">Ukupnih zahtjeva</p>
                <p className="text-3xl font-bold text-orange-900">
                  {stats.ukupnoZahtjeva || 0}
                </p>
              </div>
              <TrendingUp size={40} className="text-orange-300" />
            </div>
          </div>
        </div>
      )}

      {/* Korisnici */}
      {activeTab === "users" && (
        <div className="overflow-x-auto border rounded-lg">
          <table className="w-full">
            <thead className="bg-gray-100 border-b">
              <tr>
                <th className="text-left p-4">Email</th>
                <th className="text-left p-4">Ime</th>
                <th className="text-left p-4">Uloga</th>
                <th className="text-left p-4">Registrovan</th>
                <th className="text-left p-4">Akcija</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan="5" className="p-4 text-center text-gray-500">
                    Nema korisnika
                  </td>
                </tr>
              ) : (
                users.map((u) => (
                  <tr key={u.id} className="border-b hover:bg-gray-50">
                    <td className="p-4">{u.email}</td>
                    <td className="p-4">{u.ime || "-"}</td>
                    <td className="p-4">
                      <span
                        className={`px-3 py-1 rounded text-xs font-semibold ${
                          u.uloga === "ADMIN"
                            ? "bg-red-100 text-red-800"
                            : u.uloga === "DONATOR"
                            ? "bg-green-100 text-green-800"
                            : "bg-blue-100 text-blue-800"
                        }`}
                      >
                        {u.uloga || "KORISNIK"}
                      </span>
                    </td>
                    <td className="p-4 text-sm text-gray-600">
                      {new Date(u.datumRegistracije).toLocaleDateString("hr-HR")}
                    </td>
                    <td className="p-4">
                      <button
                        onClick={() => handleDeleteUser(u.id)}
                        disabled={deleting === u.id}
                        className="text-red-600 hover:text-red-900 p-2"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Donacije */}
      {activeTab === "donations" && (
        <div className="overflow-x-auto border rounded-lg">
          <table className="w-full">
            <thead className="bg-gray-100 border-b">
              <tr>
                <th className="text-left p-4">Igračka</th>
                <th className="text-left p-4">Donator</th>
                <th className="text-left p-4">Status</th>
                <th className="text-left p-4">Datum</th>
                <th className="text-left p-4">Akcija</th>
              </tr>
            </thead>
            <tbody>
              {donations.length === 0 ? (
                <tr>
                  <td colSpan="5" className="p-4 text-center text-gray-500">
                    Nema donacija
                  </td>
                </tr>
              ) : (
                donations.map((d) => (
                  <tr key={d.id} className="border-b hover:bg-gray-50">
                    <td className="p-4">{d.igracka?.naziv || "Nepoznata"}</td>
                    <td className="p-4 text-sm">{d.donator?.email || "-"}</td>
                    <td className="p-4">
                      <span
                        className={`px-3 py-1 rounded text-xs font-semibold ${
                          d.status === "dostupno"
                            ? "bg-green-100 text-green-800"
                            : d.status === "rezervirano"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-gray-100 text-gray-800"
                        }`}
                      >
                        {d.status}
                      </span>
                    </td>
                    <td className="p-4 text-sm text-gray-600">
                      {new Date(d.datumKreiranja).toLocaleDateString("hr-HR")}
                    </td>
                    <td className="p-4">
                      <button
                        onClick={() => handleDeleteDonation(d.id)}
                        disabled={deleting === d.id}
                        className="text-red-600 hover:text-red-900 p-2"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Kampanje */}
      {activeTab === "campaigns" && (
        <div className="overflow-x-auto border rounded-lg">
          <table className="w-full">
            <thead className="bg-gray-100 border-b">
              <tr>
                <th className="text-left p-4">Naziv</th>
                <th className="text-left p-4">Organizator</th>
                <th className="text-left p-4">Rok trajanja</th>
                <th className="text-left p-4">Status</th>
                <th className="text-left p-4">Akcija</th>
              </tr>
            </thead>
            <tbody>
              {campaigns.length === 0 ? (
                <tr>
                  <td colSpan="5" className="p-4 text-center text-gray-500">
                    Nema kampanja
                  </td>
                </tr>
              ) : (
                campaigns.map((c) => {
                  const isActive = new Date(c.rokTrajanja) > new Date();
                  return (
                    <tr key={c.id} className="border-b hover:bg-gray-50">
                      <td className="p-4">{c.napredak || "Kampanja"}</td>
                      <td className="p-4 text-sm">{c.primatelj?.email || "-"}</td>
                      <td className="p-4 text-sm">
                        {new Date(c.rokTrajanja).toLocaleDateString("hr-HR")}
                      </td>
                      <td className="p-4">
                        <span
                          className={`px-3 py-1 rounded text-xs font-semibold ${
                            isActive
                              ? "bg-green-100 text-green-800"
                              : "bg-gray-100 text-gray-800"
                          }`}
                        >
                          {isActive ? "Aktivna" : "Završena"}
                        </span>
                      </td>
                      <td className="p-4">
                        <button
                          onClick={() => handleDeleteCampaign(c.id)}
                          disabled={deleting === c.id}
                          className="text-red-600 hover:text-red-900 p-2"
                        >
                          <Trash2 size={18} />
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
