import { Sparkles } from 'lucide-react'

export default function CampaignsTeaser() {
  return (
    <section className="py-20 bg-gradient-to-b from-red-50 to-white">
      <div className="container mx-auto px-4">
        <div className="text-center max-w-3xl mx-auto">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Pridruži se <em className="underline">kampanjama</em>
          </h2>
          <p className="text-lg text-gray-600 mb-8">
            Sudjeluj u organiziranim akcijama prikupljanja igračaka za udruge i ackije
          </p>

          {/* Coming Soon */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-12 shadow-md border-2 border-red-100">
            <div className="flex justify-center mb-4">
              <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
                <Sparkles className="w-8 h-8 text-red-600" />
              </div>
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-2">Dolaze uskoro</h3>
            <p className="text-gray-600">
              Uskoro ćemo pokrenuti kampanje za prikupljanje igračaka.<br />
              Pratite nas za najnovija ažuriranja!
            </p>
          </div>
        </div>
      </div>
    </section>
  )
}
