import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Search } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '@/context/AuthContext'

export default function Navbar() {
  const [searchQuery, setSearchQuery] = useState('')
  const { user, loading, logout } = useAuth()

  const handleSearch = (e) => {
    e.preventDefault()
    // TODO: Implement search functionality
    console.log('Searching for:', searchQuery)
  }

  return (
    <nav className="fixed top-0 left-0 right-0 bg-white/60 backdrop-blur-sm shadow-md z-50 border-b border-gray-200/50">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16 gap-4">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 flex-shrink-0">
            <img
              src="/Logo.png"
              alt="PlayForward Logo"
              className="h-10 w-auto"
            />
            <span className="text-xl font-bold text-gray-900 hidden sm:inline">
              PlayForward
            </span>
          </Link>

          {/* Search Bar */}
          <form
            onSubmit={handleSearch}
            className="flex-1 max-w-xl"
          >
            <div className="relative">
              <input
                type="text"
                placeholder="Pretraži igračke..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-4 py-2 pl-10 pr-4 text-gray-700 bg-gray-100 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-all"
              />
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            </div>
          </form>

          {/* Right side */}
          <div className="flex items-center gap-2 flex-shrink-0">
            <Button
              variant="ghost"
              className="text-gray-700 hover:text-red-600 hidden md:inline-flex"
              asChild
            >
              <Link to="/">Početna</Link>
            </Button>

            {user ? (
              <>
                <Button variant="ghost" className="hidden md:inline-flex" asChild>
                  <Link to="/dashboard">Dashboard</Link>
                </Button>
                <div className="flex items-center gap-3 pl-2">
                  {user.picture && (
                    <img
                      src={user.picture}
                      alt={user.name ?? user.email}
                      className="w-8 h-8 rounded-full border hidden md:block"
                      referrerPolicy="no-referrer"
                    />
                  )}
                  <div className="text-sm text-gray-700 hidden lg:block">
                    <p className="font-semibold leading-tight">{user.name ?? user.email}</p>
                    <p className="text-xs text-gray-500">{user.email}</p>
                  </div>
                </div>
                <Button
                  variant="outline"
                  className="rounded-full"
                  onClick={logout}
                  disabled={loading}
                >
                  Odjava
                </Button>
              </>
            ) : (
              <Button
                className="bg-red-600 hover:bg-red-700 text-white rounded-full"
                asChild
              >
                <Link to="/prijava">Prijava</Link>
              </Button>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}