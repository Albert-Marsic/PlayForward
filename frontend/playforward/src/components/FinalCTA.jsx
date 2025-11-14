import { Button } from '@/components/ui/button'
import { Link } from 'react-router-dom'
import { HandHeart } from 'lucide-react'

export default function FinalCTA() {
  return (
    <section className="relative py-24 overflow-hidden">
      {/* Background with gradient overlay */}
      <div className="absolute inset-0 bg-gradient-to-br from-red-600 via-red-500 to-red-700"></div>

      {/* Decorative elements */}
      <div className="absolute top-0 left-0 w-full h-full">
        <div className="absolute top-10 left-10 w-32 h-32 bg-white/10 rounded-full blur-2xl"></div>
        <div className="absolute bottom-10 right-10 w-48 h-48 bg-white/10 rounded-full blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 w-64 h-64 bg-white/5 rounded-full blur-3xl transform -translate-x-1/2 -translate-y-1/2"></div>
      </div>

      <div className="container mx-auto px-4 relative z-10">
        <div className="max-w-4xl mx-auto text-center">
          {/* Sparkle icon */}
          <div className="mb-6 flex justify-center">
            <div className="w-32 h-32 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
              <HandHeart className="w-16 h-16 text-white" />
            </div>
          </div>

          {/* Main heading */}
          <h2 className="text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-6 leading-tight">
            Spreman za <span className="text-black">pokrenuti<br />promjenu</span>?
          </h2>

          {/* Subheading */}
          <p className="text-xl md:text-2xl text-red-50 mb-10 leading-relaxed max-w-2xl mx-auto">
            Tvoja igračka može unijeti osmijeh na nečije lice.<br />
            Započni danas i budi dio nečeg lijepog.
          </p>

          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
            <Button
              size="lg"
              className="bg-white text-red-600 hover:bg-gray-100 px-8 py-6 text-lg rounded-full shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 font-semibold"
              asChild
            >
              <Link to="/prijava">Prijavi se sada</Link>
            </Button>
            <Button
              size="lg"
              variant="outline"
              className="border-2 border-white text-black hover:bg-white/10 px-8 py-6 text-lg rounded-full shadow-lg hover:shadow-xl transition-all duration-300 backdrop-blur-sm"
              asChild
            >
              <Link to="/igracke">Pregledaj igračke</Link>
            </Button>
          </div>
        </div>
      </div>
    </section>
  )
}
