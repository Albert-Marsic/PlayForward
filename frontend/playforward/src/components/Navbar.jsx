import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Search, ShoppingCart } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '@/context/AuthContext'
import { useCart } from '@/context/CartContext'

export default function Navbar() {
  const [searchQuery, setSearchQuery] = useState('')
  const { user, loading, logout } = useAuth()
  const { cartItems } = useCart()

  const handleSearch = (e) => {
    e.preventDefault()
    console.log('Searching for:', searchQuery)
  }

  return (
    <nav className="fixed top-0 left-0 right-0 bg-white/60 backdrop-blur-sm shadow-md z-50 border-b border-gray-200/50">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16 gap-4">

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <img src="/Logo.png" alt="PlayForward Logo" className="h-10" />
            <span className="text-xl font-bold hidden sm:inline">
              PlayForward
            </span>
          </Link>

          {/* Search */}
          <form onSubmit={handleSearch} className="flex-1 max-w-xl">
            <div className="relative">
              <input
                type="text"
                placeholder="Pretraži igračke..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-4 py-2 pl-10 rounded-full border"
              />
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            </div>
          </form>

          {/* Right */}
          <div className="flex items-center gap-3">

            {/* Košarica */}
            <Link to="/kosarica" className="relative">
              <ShoppingCart className="w-6 h-6" />
              {cartItems.length > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-600 text-white text-xs rounded-full px-1.5">
                  {cartItems.length}
                </span>
              )}
            </Link>

            {user ? (
              <>
                <Button variant="ghost" asChild>
                  <Link to="/profil">Profil</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/poruke">Poruke</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/kampanje">Kampanje</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/moje-donacije">Moje donacije</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/moji-zahtjevi">Moji zahtjevi</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/dashboard">Dashboard</Link>
                </Button>
                <Button variant="outline" onClick={logout} disabled={loading}>
                  Odjava
                </Button>
              </>
            ) : (
              <Button asChild>
                <Link to="/prijava">Prijava</Link>
              </Button>
            )}
          </div>

        </div>
      </div>
    </nav>
  )
}
