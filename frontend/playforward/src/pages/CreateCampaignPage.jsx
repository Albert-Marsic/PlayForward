import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { createCampaign, saveCampaignToyList } from "@/api/campaigns";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api";

export default function CreateCampaignPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const today = new Date().toISOString().split("T")[0];
  const [naziv, setNaziv] = useState("");
  const [opis, setOpis] = useState("");
  const [rokTrajanja, setRokTrajanja] = useState("");
  const [items, setItems] = useState([{ nazivIgracke: "", kolicina: 1 }]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [role, setRole] = useState(null);
  const [roleLoading, setRoleLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      setRole(null);
      setRoleLoading(false);
      return;
    }

    let isMounted = true;
    const fetchRole = async () => {
      setRoleLoading(true);
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
      } finally {
        if (isMounted) setRoleLoading(false);
      }
    };

    fetchRole();
    return () => {
      isMounted = false;
    };
  }, [user]);

  const handleItemChange = (index, field, value) => {
    setItems(prev => prev.map((item, i) => (
      i === index ? { ...item, [field]: value } : item
    )));
  };

  const handleAddItem = () => {
    setItems(prev => [...prev, { nazivIgracke: "", kolicina: 1 }]);
  };

  const handleRemoveItem = (index) => {
    setItems(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const trimmedNaziv = naziv.trim();
    const trimmedOpis = opis.trim();
    const cleanedItems = items
      .map(item => ({
        nazivIgracke: item.nazivIgracke ? item.nazivIgracke.trim() : "",
        kolicina: Number(item.kolicina)
      }))
      .filter(item => item.nazivIgracke);

    if (!trimmedNaziv || !trimmedOpis || !rokTrajanja) {
      setError("Naziv, opis i rok trajanja su obavezni.");
      return;
    }

    if (cleanedItems.length === 0) {
      setError("Unesite barem jednu igračku.");
      return;
    }

    if (cleanedItems.some(item => Number.isNaN(item.kolicina) || item.kolicina <= 0)) {
      setError("Količina mora biti veća od 0.");
      return;
    }

    try {
      setLoading(true);
      const created = await createCampaign({
        naziv: trimmedNaziv,
        opis: trimmedOpis,
        rokTrajanja
      });

      await saveCampaignToyList(created.id, cleanedItems);
      alert("Kampanja je uspješno kreirana! 🎉");
      navigate(`/kampanja/${created.id}`);
    } catch (err) {
      setError(err.message || "Greška pri kreiranju kampanje.");
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="p-6 max-w-3xl mx-auto text-center space-y-4">
        <h1 className="text-2xl font-bold">Prijava je potrebna</h1>
        <p className="text-gray-600">
          Za kreiranje kampanje potrebno je prijaviti se kao udruga.
        </p>
        <Button asChild>
          <Link to="/prijava">Prijava</Link>
        </Button>
      </div>
    );
  }

  if (roleLoading) {
    return <p className="p-6">Učitavanje...</p>;
  }

  if (role !== "RECIPIENT") {
    return (
      <div className="p-6 max-w-3xl mx-auto text-center space-y-4">
        <h1 className="text-2xl font-bold">Nedovoljna prava</h1>
        <p className="text-gray-600">
          Samo udruge (primatelji) mogu kreirati kampanje.
        </p>
        <Button asChild variant="outline">
          <Link to="/kampanje">Nazad na kampanje</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="flex flex-col gap-4 mb-6 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">Nova kampanja</h1>
          <p className="text-gray-600">
            Definirajte naziv, opis i popis potrebnih igračaka.
          </p>
        </div>
        <Button variant="outline" asChild>
          <Link to="/kampanje">Nazad na kampanje</Link>
        </Button>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <label className="text-sm font-medium text-gray-700">Naziv kampanje *</label>
            <Input
              value={naziv}
              onChange={(e) => setNaziv(e.target.value)}
              placeholder="Npr. Igračke za dječji dom"
              required
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-medium text-gray-700">Rok trajanja *</label>
            <Input
              type="date"
              value={rokTrajanja}
              onChange={(e) => setRokTrajanja(e.target.value)}
              min={today}
              required
            />
          </div>
        </div>

        <div className="space-y-2">
          <label className="text-sm font-medium text-gray-700">Opis kampanje *</label>
          <textarea
            className="border rounded px-3 py-2 w-full min-h-[120px]"
            value={opis}
            onChange={(e) => setOpis(e.target.value)}
            placeholder="Opišite kome kampanja pomaže i što želite prikupiti."
            required
          />
        </div>

        <div className="border rounded-lg p-4 space-y-4">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-lg font-semibold">Popis igračaka</h2>
              <p className="text-sm text-gray-600">Dodajte vrste i količine igračaka.</p>
            </div>
            <Button type="button" variant="outline" onClick={handleAddItem}>
              Dodaj igračku
            </Button>
          </div>

          <div className="space-y-3">
            {items.map((item, index) => (
              <div
                key={`item-${index}`}
                className="grid gap-3 md:grid-cols-12 md:items-end"
              >
                <div className="md:col-span-7 space-y-1">
                  <label className="text-sm text-gray-600">Naziv igračke</label>
                  <Input
                    value={item.nazivIgracke}
                    onChange={(e) => handleItemChange(index, "nazivIgracke", e.target.value)}
                    placeholder="Npr. Lego kockice"
                    required={index === 0}
                  />
                </div>
                <div className="md:col-span-3 space-y-1">
                  <label className="text-sm text-gray-600">Količina</label>
                  <Input
                    type="number"
                    min="1"
                    value={item.kolicina}
                    onChange={(e) => handleItemChange(index, "kolicina", e.target.value)}
                    required={index === 0}
                  />
                </div>
                <div className="md:col-span-2">
                  <Button
                    type="button"
                    variant="ghost"
                    className="w-full text-red-600 hover:text-red-800"
                    onClick={() => handleRemoveItem(index)}
                    disabled={items.length === 1}
                  >
                    Ukloni
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? "Spremanje..." : "Objavi kampanju"}
        </Button>
      </form>
    </div>
  );
}
