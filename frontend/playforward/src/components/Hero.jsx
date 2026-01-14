import { Button } from '@/components/ui/button'
import { useAuth } from '@/context/AuthContext'
import { Link } from 'react-router-dom'


export default function Hero() {
  const { user } = useAuth();
  return (
    <section className="relative flex items-center min-h-[80vh] justify-center bg-gradient-to-br from-white via-red-50 to-red-100 overflow-hidden">
      <div className="container mx-auto px-4 z-10">
        <div className="max-w-4xl mx-auto">
          <div className="flex flex-col md:flex-row items-center gap-4 md:gap-6">
            {/* Content */}
            <div className="text-center md:text-left order-2 md:order-1">
              {/* Main Heading */}
              {!user ? 
                <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-gray-900 mb-4 leading-tight">
                  Pokloni radost,<br />
                  <span className="text-red-600">podijeli igračku</span>
                </h1>
                :
                <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-gray-900 mb-4 leading-tight">
                  Dobrodošao,<br />
                  <span className="text-red-600">doniraj nešto novo!</span>
                </h1>
                
              }
              
              {/* Subheading */}
              <p className="text-lg md:text-xl text-gray-700 mb-8 leading-relaxed">
                Platforma koja spaja srca - donirajte igračke djeci kojima su potrebne
              </p>

              {/* CTA Buttons */}
              <div className="flex flex-col sm:flex-row gap-4 justify-center md:justify-start items-center">
                <Button
                  size="lg"
                  className="bg-red-600 hover:bg-red-700 text-white px-8 py-6 text-lg rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105"
                  asChild
                >
                  <Link to={user ? "/doniraj" : "/prijava"}>Doniraj igračku</Link>
                </Button>
                <Button
                  size="lg"
                  variant="outline"
                  className="border-2 border-red-600 text-red-600 hover:bg-red-50 px-8 py-6 text-lg rounded-full shadow-md hover:shadow-lg transition-all duration-300"
                  asChild
                >
                  <Link to="/igracke">Potraži igračku</Link>
                </Button>
              </div>
            </div>

            {/* Logo */}
            <div className="flex-shrink-0 order-1 md:order-2">
              <img
                src="/Logo.png"
                alt="PlayForward Logo"
                className="h-40 md:h-52 lg:h-64 w-auto drop-shadow-lg"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
