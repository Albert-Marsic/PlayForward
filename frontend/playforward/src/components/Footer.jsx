import { Link } from 'react-router-dom'
import { Heart, Mail } from 'lucide-react'

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-300">
      <div className="container mx-auto px-4 py-12">
        <div className="grid md:grid-cols-4 gap-8 mb-8">
          {/* Logo and Description */}
          <div className="md:col-span-2">
            <div className="flex items-center gap-2 mb-4">
              <img
                src="/Logo.png"
                alt="PlayForward Logo"
                className="h-12 w-auto"
              />
              <span className="text-2xl font-bold text-white">PlayForward</span>
            </div>
            <p className="text-gray-400 mb-4 max-w-md">
              Platforma koja spaja srca i dijeli radost kroz donacije dječjih igračaka.
              Budimo zajedno promjena koju želimo vidjeti.
            </p>
            <div className="flex items-center gap-1 text-red-400">
              <span>Napravljeno s</span>
              <Heart className="w-4 h-4 fill-current" />
              <span>od tima Legionari, FER</span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-white font-bold mb-4">Brze poveznice</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="hover:text-red-400 transition-colors">
                  Početna
                </Link>
              </li>
              <li>
                <Link to="/prijava" className="hover:text-red-400 transition-colors">
                  Prijava
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-white font-bold mb-4">Kontakt</h3>
            <ul className="space-y-2">
              <li>
                <a href="mailto:albert.marsic@fer.hr" className="hover:text-red-400 transition-colors flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  albert.marsic@fer.hr
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-800 pt-8 mt-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-gray-500 text-sm">
              © 2025 Tim Legionari. Sva prava pridržana.
            </p>
          </div>
        </div>
      </div>
    </footer>
  )
}
