import { Card, CardContent } from '@/components/ui/card'

export default function ToyCard({ toy }) {
  return (
    <Card className="group overflow-hidden rounded-lg shadow-sm hover:shadow-md transition-all duration-300 border border-gray-200 hover:border-red-300">
      {/* Image with skeleton animation */}
      <div className="relative overflow-hidden h-24 bg-gradient-to-r from-gray-200 via-gray-50 to-gray-200 animate-shimmer bg-[length:200%_100%]">
        {toy.image && (
          <img
            src={toy.image}
            alt={toy.name}
            className="w-full h-full object-cover"
          />
        )}
      </div>

      <CardContent className="p-3">

        {/* Toy Name - skeleton */}
        <div className="h-4 bg-gradient-to-r from-gray-200 via-gray-50 to-gray-200 rounded animate-shimmer bg-[length:200%_100%] w-3/4"></div>
      </CardContent>
    </Card>
  )
}
