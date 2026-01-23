import { Card, CardContent } from '@/components/ui/card'

export default function ToyCard({ toy }) {
  const image = toy?.fotografija || toy?.image || null
  const name = toy?.naziv || toy?.name || null
  const category = toy?.kategorija || null
  const hasData = Boolean(name)

  return (
    <Card className="group overflow-hidden rounded-lg shadow-sm hover:shadow-md transition-all duration-300 border border-gray-200 hover:border-red-300">
      {/* Image with skeleton animation */}
      <div
        className={`relative overflow-hidden h-24 ${
          hasData ? "bg-gray-100" : "bg-gradient-to-r from-gray-200 via-gray-50 to-gray-200 animate-shimmer bg-[length:200%_100%]"
        }`}
      >
        {image && (
          <img
            src={image}
            alt={name || "Igračka"}
            className="w-full h-full object-cover"
          />
        )}
        {!image && hasData && (
          <div className="flex items-center justify-center h-full text-xs text-gray-500">
            Nema slike
          </div>
        )}
      </div>

      <CardContent className="p-3">
        {hasData ? (
          <div>
            <p className="text-sm font-semibold text-gray-900">{name}</p>
            {category && (
              <p className="text-xs text-gray-500">{category}</p>
            )}
          </div>
        ) : (
          <div className="h-4 bg-gradient-to-r from-gray-200 via-gray-50 to-gray-200 rounded animate-shimmer bg-[length:200%_100%] w-3/4"></div>
        )}
      </CardContent>
    </Card>
  )
}
