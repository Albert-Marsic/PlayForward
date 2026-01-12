import { useState } from "react";
import fakeData from "@/data/myFakeData";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

export default function ToysPage() {
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("Sve");

  // sve kategorije
  const categories = ["Sve", ...new Set(fakeData.map(toy => toy.kategorija))];

  // filtrirane igračke
  const filteredToys = fakeData.filter(toy => {
    const matchesSearch = toy.naziv
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchesCategory =
      selectedCategory === "Sve" || toy.kategorija === selectedCategory;

    return matchesSearch && matchesCategory;
  });

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
      </div>

      {/* popis igračaka */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {filteredToys.length === 0 && (
          <p className="text-gray-500 col-span-full text-center">
            Nema rezultata.
          </p>
        )}

        {filteredToys.map(toy => (
          <Link
            to={`/igracke/${toy.idIgracka}`}
            key={toy.idIgracka}
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
              Stanje: {toy.stanje}
            </p>
            <p className="text-sm text-gray-600">
              Status: {toy.status}
            </p>
            <p className="text -sm text-gray-600">
              Cijena: {toy.cijena.toFixed(2)} €
            </p>
          </Link>
        ))}
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
