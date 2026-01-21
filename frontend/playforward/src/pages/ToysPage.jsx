import { useState, useEffect, useMemo } from "react";
//import fakeData from "@/data/myFakeData";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { getToys } from "@/api/toys";

export default function ToysPage() {
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("Sve");
  const [sortBy, setSortBy] = useState("naziv"); // naziv, najnovije, stanje
  const [toys, setToys] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchToys = async () => {
      try {
        setLoading(true);
        const data = await getToys();
        setToys(data || []);
        setError(null);
      } catch (err) {
        setError("Greška pri učitavanju igračaka");
        console.error(err);
        setToys([]);
      } finally {
        setLoading(false);
      }
    };

    fetchToys();
  }, []);

  // sve kategorije - računanje se čini samo kad se toys promijene
  const categories = useMemo(() => {
    return ["Sve", ...new Set(toys.map(toy => toy.kategorija))];
  }, [toys]);

  // filtrirane igračke
  const filteredToys = useMemo(() => {
    let result = toys.filter(toy => {
      const matchesSearch = toy.naziv
        .toLowerCase()
        .includes(search.toLowerCase());

      const matchesCategory =
        selectedCategory === "Sve" || toy.kategorija === selectedCategory;

      return matchesSearch && matchesCategory;
    });

    // sortiranje
    if (sortBy === "naziv") {
      result.sort((a, b) => a.naziv.localeCompare(b.naziv));
    } else if (sortBy === "najnovije") {
      result.sort((a, b) => new Date(b.datumKreiranjaIgracke) - new Date(a.datumKreiranjaIgracke));
    } else if (sortBy === "stanje") {
      const stanjeOrder = { NOVO: 1, KORISTENO: 2 };
      result.sort((a, b) => (stanjeOrder[a.stanje] || 99) - (stanjeOrder[b.stanje] || 99));
    }

    return result;
  }, [toys, search, selectedCategory, sortBy]);

  if (loading) return <p className="p-6">Učitavanje...</p>;
  if (error) return <p className="p-6 text-red-500">{error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Popis igračaka</h1>

      {/* filteri */}
      <div className="flex flex-col md:flex-row gap-4 mb-6">
        {/* pretraga */}
        <input
          type="text"
          placeholder="Pretraži igračku"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border rounded px-3 py-2 w-full md:w-1/3"
        />

        {/* kategorije */}
        <select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          className="border rounded px-3 py-2 w-full md:w-1/4"
        >
          {categories.map(cat => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>

        {/* sortiranje */}
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          className="border rounded px-3 py-2 w-full md:w-1/4"
        >
          <option value="naziv">Sortiraj: Naziv (A-Z)</option>
          <option value="najnovije">Sortiraj: Najnovije prvo</option>
          <option value="stanje">Sortiraj: Stanje</option>
        </select>
      </div>

      {/* popis igračaka */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {filteredToys.length === 0 && (
          <p className="text-gray-500 col-span-full text-center">
            Nema rezultata.
          </p>
        )}

        {filteredToys.map(toy => {
          const toyId = toy.id ?? toy.idIgracka;
          const stanjeLabel = toy.stanje === "NOVO"
            ? "Novo"
            : toy.stanje === "KORISTENO"
              ? "Korišteno"
              : toy.stanje;
          return (
          <Link
            to={`/igracke/${toyId}`}
            key={toyId}
            className="border rounded-lg p-4 shadow bg-white hover:shadow-lg transition cursor-pointer"
          >
            <img
              src={toy.fotografija}
              alt={toy.naziv}
              className="w-full object-cover rounded max-h-[300px]"
            />

            <h2 className="text-lg font-semibold mt-2">
              {toy.naziv}
            </h2>

            <p className="text-sm text-gray-600">
              Kategorija: {toy.kategorija}
            </p>
            <p className="text-sm text-gray-600">
              Stanje: {stanjeLabel}
            </p>
            <p className="text-sm text-gray-600">
              Status: {toy.status}
            </p>
          </Link>
        )})}
      </div>

      {/* donacija */}
      <div className="flex justify-center mt-10">
        <Button variant="outline" type="button">
          <Link to="/doniraj">Doniraj igračku</Link>
        </Button>
      </div>
    </div>
  );
}
