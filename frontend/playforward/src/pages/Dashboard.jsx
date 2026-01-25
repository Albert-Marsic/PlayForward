import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { GOOGLE_LOGIN_URL } from "@/lib/config";
import { api } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";

export default function Dashboard() {
  const { user, loading, logout } = useAuth();
  const navigate = useNavigate();
  const [role, setRole] = useState(null);
  const [roleLoading, setRoleLoading] = useState(false);
  const [roleError, setRoleError] = useState(null);
  const [roleSubmitting, setRoleSubmitting] = useState(false);

  const handleGoogleLogin = () => {
    window.location.href = GOOGLE_LOGIN_URL;
  };

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  useEffect(() => {
    if (!user) {
      setRole(null);
      setRoleLoading(false);
      setRoleError(null);
      return;
    }

    const fetchRole = async () => {
      setRoleLoading(true);
      setRoleError(null);
      try {
        const response = await api("/auth/role");
        if (response.ok) {
          const data = await response.json();
          setRole(data.role ?? null);
        } else {
          setRole(null);
        }
      } catch (error) {
        console.error("Failed to fetch role", error);
        setRoleError("Ne mogu dohvatiti ulogu.");
      } finally {
        setRoleLoading(false);
      }
    };

    fetchRole();
  }, [user]);

  const handleRoleSelect = async (selectedRole) => {
    setRoleSubmitting(true);
    setRoleError(null);
    try {
      const response = await api("/auth/role", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ role: selectedRole }),
      });

      if (!response.ok) {
        let message = "Ne mogu spremiti ulogu.";
        try {
          const data = await response.json();
          if (data?.message) {
            message = data.message;
          }
        } catch (parseError) {
          console.error("Failed to parse role error", parseError);
        }
        setRoleError(message);
        return;
      }

      const data = await response.json();
      setRole(data.role ?? selectedRole);
      window.location.reload()
    } catch (error) {
      console.error("Failed to save role", error);
      setRoleError("Došlo je do pogreške prilikom spremanja uloge.");
    } finally {
      setRoleSubmitting(false);
    }
  };

  const roleLabel = role === "DONATOR"
    ? "Donator"
    : role === "RECIPIENT"
      ? "Primatelj"
      : null;
  const isAdmin =
    user?.admin === true || user?.role === "ADMIN" || user?.uloga === "ADMIN";

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <p className="text-lg text-gray-600">Provjeravam prijavu...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="max-w-lg mx-auto p-6 text-center space-y-4">
        <h1 className="text-3xl font-semibold">Pristup zahtijeva prijavu</h1>
        <p className="text-gray-600">
          Molimo prijavite se Googleom ili nastavite na stranicu za prijavu.
        </p>
        <div className="flex flex-col gap-3 items-center">
          <Button className="w-full max-w-xs" onClick={handleGoogleLogin}>
            Nastavi s Googleom
          </Button>
          <Button variant="outline" asChild className="w-full max-w-xs">
            <Link to="/prijava">Idi na stranicu za prijavu</Link>
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      <header className="flex items-center gap-4">
        {user.picture && (
          <img
            src={user.picture}
            alt={user.name ?? user.email}
            className="w-16 h-16 rounded-full border"
            referrerPolicy="no-referrer"
          />
        )}
        <div>
          <p className="text-sm text-gray-500">Prijavljeni ste kao</p>
          <h1 className="text-2xl font-semibold">{user.name ?? user.email}</h1>
          <p className="text-gray-600">{user.email}</p>
          {roleLabel && (
            <p className="text-sm text-gray-500 mt-1">Uloga: {roleLabel}</p>
          )}
        </div>
      </header>

      {isAdmin && (
        <section className="rounded-2xl border bg-white shadow-sm p-6 space-y-3">
          <h2 className="text-xl font-semibold">Admin panel</h2>
          <p className="text-gray-600">
            Upravljajte korisnicima, donacijama i kampanjama.
          </p>
          <Button asChild>
            <Link to="/admin">Otvori admin panel</Link>
          </Button>
        </section>
      )}

      <section className="rounded-2xl border bg-white shadow-sm p-6 space-y-4">
        <h2 className="text-xl font-semibold">Vaša uloga</h2>
        {roleLoading ? (
          <p className="text-gray-600">Učitavam ulogu...</p>
        ) : roleLabel ? (
          <p className="text-gray-600">
            Registrirani ste kao <strong>{roleLabel}</strong>.
          </p>
        ) : (
          <>
            <p className="text-gray-600">
              Odaberite želite li donirati igračke ili primati donacije.
            </p>
            <div className="grid gap-3 sm:grid-cols-2">
              <Button
                className="w-full"
                onClick={() => handleRoleSelect("DONATOR")}
                disabled={roleSubmitting}
              >
                Želim donirati
              </Button>
              <Button
                className="w-full"
                variant="outline"
                onClick={() => handleRoleSelect("RECIPIENT")}
                disabled={roleSubmitting}
              >
                Trebam donaciju
              </Button>
            </div>
          </>
        )}
        {roleError && (
          <p className="text-sm text-red-600">{roleError}</p>
        )}
      </section>

      <section className="rounded-2xl border bg-white shadow-sm p-6 space-y-3">
        <h2 className="text-xl font-semibold">Vaš Google račun</h2>
        <p className="text-gray-600">
          Nakon uspješne prijave možete nastaviti koristiti PlayForward s
          ovim računom. Ako želite promijeniti račun jednostavno se odjavite i
          ponovno prijavite.
        </p>
        <Button variant="outline" onClick={handleLogout}>
          Odjava
        </Button>
      </section>
    </div>
  );
}
