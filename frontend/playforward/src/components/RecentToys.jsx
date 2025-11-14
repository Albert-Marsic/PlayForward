import ToyCard from './ToyCard'
import { Button } from '@/components/ui/button'
import { ArrowRight } from 'lucide-react'
import { Link } from 'react-router-dom'

export default function RecentToys() {
  return (
    <section className="py-20 bg-gradient-to-b from-gray-50 to-white">
      <div className="container mx-auto px-4">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Nedavno objavljene igračke
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Pogledaj najnovije igračke koje čekaju na nove vlasnike
          </p>
        </div>

        {/* Toy Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4 max-w-7xl mx-auto mb-12">
          {Array(8).fill({}).map((_, i) => (
            <ToyCard key={i} toy={{image: null, name: null}} />
          ))}
        </div>

        {/* See More Button */}
        <div className="text-center">
          <Link to="/igracke">
            <Button
              size="lg"
              variant="outline"
              className="border-2 border-red-600 text-red-600 hover:bg-red-50 px-8 py-6 text-lg rounded-full shadow-md hover:shadow-lg transition-all duration-300 cursor-pointer"
            >
              Pregledaj sve igračke
              <ArrowRight className="ml-2 w-5 h-5" />
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
