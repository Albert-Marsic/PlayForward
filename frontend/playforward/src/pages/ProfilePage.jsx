import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api";
import { User, Mail, Shield, LogOut } from "lucide-react";

export default function ProfilePage() {
  const { user, logout, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [switching, setSwitching] = useState(false);

  useEffect(() => {
    if (!authLoading && !user) {
      navigate("/prijava");
      return;
    }

    const fetchProfile = async () => {
      try {
        setLoading(true);
        const response = await api("/korisnici/profil");
        setProfileData(response.data);
        setError(null);
      } catch (err) {
        console.error("Greška pri dohvaćanju profila:", err);
        setError("Nije moguće učitati profil");
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchProfile();
    }
  }, [user, authLoading, navigate]);

  const handleRoleSwitch = async (newRole) => {
    try {
      setSwitching(true);
      const response = await api("/korisnici/promijeni-ulogu", {
        method: "POST",
        data: { uloga: newRole },
      });

      if (response.status === 200) {
        setProfileData(response.data);
        alert(`Sada ste ${newRole === "DONATOR" ? "donator" : "primatelj"}! ✅`);
      }
    } catch (err) {
      alert("Greška pri promeni uloge: " + err.message);
    } finally {
      setSwitching(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  if (authLoading || loading) {
    return <div className="p-6 text-center">Učitavanje...</div>;
  }

  if (!user || !profileData) {
    return (
      <div className="p-6 text-center">
        <p className="text-red-600 mb-4">Korisnik nije pronađen</p>
        <Button onClick={() => navigate("/")}>Nazad na početak</Button>
      </div>
    );
  }

  const currentRole = profileData.uloga || "DONATOR";
  const otherRole = currentRole === "DONATOR" ? "PRIMATELJ" : "DONATOR";

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Moj profil</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {/* Korisnički podaci */}
      <div className="bg-white border rounded-lg shadow-md p-6 mb-8">
        <div className="flex items-start gap-6 mb-6">
          {user.picture && (
            <img
              src={user.picture}
              alt={user.name}
              className="w-20 h-20 rounded-full object-cover"
            />
          )}
          <div>
            <div className="flex items-center gap-2 mb-2">
              <User size={20} className="text-gray-600" />
              <h2 className="text-2xl font-semibold">{user.name}</h2>
            </div>
            <div className="flex items-center gap-2">
              <Mail size={16} className="text-gray-600" />
              <p className="text-gray-700">{user.email}</p>
            </div>
          </div>
        </div>

        <hr className="my-4" />

        {/* Trenutna uloga */}
        <div className="flex items-center gap-2 mb-6">
          <Shield size={20} className="text-blue-600" />
          <span className="text-lg">
            <strong>Trenutna uloga:</strong>{" "}
            <span className="text-blue-600 font-semibold">
              {currentRole === "DONATOR" ? "Donator" : "Primatelj"}
            </span>
          </span>
        </div>

        {/* Statistika */}
        {profileData.statistika && (
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div className="bg-green-50 p-4 rounded border border-green-200">
              <p className="text-sm text-gray-600">Aktivne donacije</p>
              <p className="text-2xl font-bold text-green-600">
                {profileData.statistika.aktivneDonacije || 0}
              </p>
            </div>
            <div className="bg-blue-50 p-4 rounded border border-blue-200">
              <p className="text-sm text-gray-600">Aktivni zahtjevi</p>
              <p className="text-2xl font-bold text-blue-600">
                {profileData.statistika.aktivniZahtjevi || 0}
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Promjena uloge */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
        <h3 className="text-lg font-semibold mb-4">Promijeni ulogu</h3>
        <p className="text-gray-700 mb-4">
          Trenutno ste {currentRole === "DONATOR" ? "donator" : "primatelj"}.
          Možete se prebaciti u drugu ulogu.
        </p>
        <Button
          onClick={() => handleRoleSwitch(otherRole)}
          disabled={switching}
          className="bg-blue-600 hover:bg-blue-700"
        >
          {switching ? "Prebacujem..." : `Postani ${otherRole === "DONATOR" ? "donator" : "primatelj"}`}
        </Button>
      </div>

      {/* Odjava */}
      <div className="flex gap-3">
        <Button
          variant="outline"
          onClick={() => navigate("/")}
          className="flex-1"
        >
          Nazad
        </Button>
        <Button
          variant="destructive"
          onClick={handleLogout}
          className="flex-1 flex items-center justify-center gap-2"
        >
          <LogOut size={18} />
          Odjava
        </Button>
      </div>
    </div>
  );
}
