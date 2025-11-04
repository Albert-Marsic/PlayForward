import { Card, CardContent } from '@/components/ui/card'
import { UserPlus, Search, MessageCircle } from 'lucide-react'

export default function HowItWorks() {
  const steps = [
    {
      number: 1,
      icon: UserPlus,
      title: "Registracija",
      description: "Registriraj se kao donator ili primatelj"
    },
    {
      number: 2,
      icon: Search,
      title: "Objavi/Pretraži",
      description: "Objavi igračku ili pretraži dostupne donacije"
    },
    {
      number: 3,
      icon: MessageCircle,
      title: "Poveži se",
      description: "Dogovori preuzimanje preko ugrađenog chat sustava"
    }
  ]

  return (
    <section className="py-20 bg-white">
      <div className="container mx-auto px-4">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Kako funkcionira?
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Jednostavno i brzo do nove igračke ili do srca djece kojima poklanjate
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
          {steps.map((step) => {
            const Icon = step.icon
            return (
              <Card
                key={step.number}
                className="relative border-2 border-gray-100 hover:border-red-200 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:-translate-y-2"
              >
                <CardContent className="p-8 text-center">
                  {/* Step Number Badge */}
                  <div className="absolute -top-4 left-1/2 transform -translate-x-1/2 w-12 h-12 bg-red-600 text-white rounded-full flex items-center justify-center font-bold text-xl shadow-lg">
                    {step.number}
                  </div>

                  {/* Icon */}
                  <div className="mt-4 mb-6 flex justify-center">
                    <div className="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center">
                      <Icon className="w-10 h-10 text-red-600" />
                    </div>
                  </div>

                  {/* Title */}
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">
                    {step.title}
                  </h3>

                  {/* Description */}
                  <p className="text-gray-600 leading-relaxed">
                    {step.description}
                  </p>
                </CardContent>
              </Card>
            )
          })}
        </div>

        {/* Connecting Lines (visible on desktop) */}
        <div className="hidden md:block relative -mt-[280px] mb-[280px] max-w-6xl mx-auto">
          <div className="absolute top-1/2 left-1/4 right-1/4 h-1 bg-gradient-to-r from-red-200 via-red-300 to-red-200 transform -translate-y-1/2 z-0"></div>
        </div>
      </div>
    </section>
  )
}
