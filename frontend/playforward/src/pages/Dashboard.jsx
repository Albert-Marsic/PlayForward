import { useAuth } from "@/context/AuthContext";
import { GOOGLE_LOGIN_URL } from "@/lib/config";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";

export default function Dashboard() {
  const { user, loading, logout } = useAuth();
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    window.location.href = GOOGLE_LOGIN_URL;
  };

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

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
        </div>
      </header>

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
