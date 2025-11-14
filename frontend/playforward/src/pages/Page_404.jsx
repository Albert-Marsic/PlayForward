import { Link } from "react-router-dom"

export default function Page_404() {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Glavni sadržaj */}
      <main className="flex-1 flex flex-col items-center justify-center text-center px-4 bg-gray-50">
        <img src ="/Error.png" alt="Stranica nije pronađena" className="w-58 h-auto"></img>
        
        <h1 className="text-6xl font-extrabold text-gray-900 mb-4">404</h1>
        <p className="text-xl text-gray-600 mb-8">
          Ups! Stranica koju tražite ne postoji.
        </p>

        <Link
          to="/"
          className="bg-black text-white px-6 py-2 rounded-md hover:bg-gray-800 transition"
        >
          Vrati se na početnu
        </Link>
      </main>
    </div>
  )
}