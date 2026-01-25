import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Gift } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'

export default function Navbar() {
  const { user, loading, logout } = useAuth()

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

          {/* Browse Toys Button */}
          <div className="flex-1 flex justify-center">
            <Button
              asChild
              size="lg"
              className="bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white px-6 py-2 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 animate-pulse hover:animate-none"
            >
              <Link to="/igracke" className="flex items-center gap-2">
                <Gift className="w-5 h-5" />
                <span className="font-semibold">Pogledaj igračke</span>
              </Link>
            </Button>
          </div>

          {/* Right */}
          <div className="flex items-center gap-3">
            {user ? (
              <>
                <Button variant="ghost" asChild>
                  <Link to="/poruke">Poruke</Link>
                </Button>
                <Button variant="ghost" asChild>
                  <Link to="/kampanje">Kampanje</Link>
                </Button>
                {user.role === "DONATOR" && (
                  <Button variant="ghost" asChild>
                    <Link to="/moje-donacije">Moje donacije</Link>
                  </Button>
                )}
                {user.role === "RECIPIENT" && (
                  <Button variant="ghost" asChild>
                    <Link to="/moji-zahtjevi">Moji zahtjevi</Link>
                  </Button>
                )}
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
