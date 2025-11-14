import fakeData from "@/data/myFakeData";
import { Button } from "@/components/ui/button"
import { Link } from 'react-router-dom'

export default function ToysPage() {
  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Popis igračaka</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {fakeData.map(toy => (
          <div
            key={toy.idIgracka}
            className="border rounded-lg p-4 shadow bg-white"
          >
            <img
              src={toy.fotografija}
              alt={toy.naziv}
              className="w-full h-90 object-cover rounded"
            />

            <h2 className="text-lg font-semibold mt-2">{toy.naziv}</h2>

            <p className="text-sm text-gray-600">Kategorija: {toy.kategorija}</p>
            <p className="text-sm text-gray-600">Stanje: {toy.stanje}</p>
            <p className="text-sm text-gray-600">Status: {toy.status}</p>
          </div>
        ))}
      </div>

      <div className="flex justify-center mt-10">
        <Button variant="outline" type="button">
          <Link to="/doniraj">
            Doniraj igračku
          </Link>
        </Button>
      </div>
    </div>
  );
}
