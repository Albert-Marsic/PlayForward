import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { getCampaigns, calculateCompletionPercentage } from '@/api/campaigns'
import { Sparkles, Calendar, ArrowRight } from 'lucide-react'

export default function CampaignsTeaser() {
  const [campaigns, setCampaigns] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchCampaigns = async () => {
      try {
        const data = await getCampaigns()
        // Filter active campaigns and take max 3
        const today = new Date()
        const active = (data || [])
          .filter(c => {
            if (c.status) return c.status === 'AKTIVNA'
            const deadline = new Date(c.rokTrajanja)
            return deadline >= today
          })
          .slice(0, 3)
        setCampaigns(active)
      } catch (err) {
        console.error('Error fetching campaigns:', err)
        setCampaigns([])
      } finally {
        setLoading(false)
      }
    }

    fetchCampaigns()
  }, [])

  return (
    <section className="py-20 bg-gradient-to-b from-red-50 to-white">
      <div className="container mx-auto px-4">
        <div className="text-center max-w-3xl mx-auto mb-12">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Pridruži se <em className="underline">kampanjama</em>
          </h2>
          <p className="text-lg text-gray-600">
            Sudjeluj u organiziranim akcijama prikupljanja igračaka za udruge i akcije
          </p>
        </div>

        {loading ? (
          <div className="flex justify-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
          </div>
        ) : campaigns.length === 0 ? (
          /* No active campaigns */
          <div className="max-w-3xl mx-auto">
            <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-12 shadow-md border-2 border-red-100 text-center">
              <div className="flex justify-center mb-4">
                <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
                  <Sparkles className="w-8 h-8 text-red-600" />
                </div>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">Nema aktivnih kampanja</h3>
              <p className="text-gray-600 mb-6">
                Trenutno nema aktivnih kampanja.<br />
                Posjetite stranicu kampanja za više informacija.
              </p>
              <Button asChild>
                <Link to="/kampanje">Pogledaj sve kampanje</Link>
              </Button>
            </div>
          </div>
        ) : (
          /* Active campaigns */
          <>
            <div className="grid md:grid-cols-3 gap-6 max-w-5xl mx-auto mb-8">
              {campaigns.map(campaign => {
                const percentage = campaign.popisi?.length
                  ? calculateCompletionPercentage(campaign)
                  : (typeof campaign.postotak === 'number' ? campaign.postotak : 0)
                const daysLeft = Math.ceil((new Date(campaign.rokTrajanja) - new Date()) / (1000 * 60 * 60 * 24))
                const daysLeftLabel = daysLeft > 0 ? `${daysLeft} dana` : 'Zadnji dan'
                const title = campaign.naziv || 'Kampanja'

                return (
                  <Link
                    key={campaign.id}
                    to={`/kampanja/${campaign.id}`}
                    className="group bg-white rounded-2xl p-6 shadow-md border border-gray-100 hover:shadow-xl hover:border-red-200 transition-all duration-300 hover:-translate-y-1"
                  >
                    <div className="flex justify-between items-start mb-3">
                      <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full font-medium">
                        Aktivna
                      </span>
                      <span className="text-xs text-gray-500 flex items-center gap-1">
                        <Calendar size={12} />
                        {daysLeftLabel}
                      </span>
                    </div>

                    <h3 className="text-lg font-bold text-gray-900 mb-2 group-hover:text-red-600 transition-colors line-clamp-2">
                      {title}
                    </h3>

                    {campaign.opis && (
                      <p className="text-sm text-gray-600 mb-4 line-clamp-2">
                        {campaign.opis}
                      </p>
                    )}

                    {/* Progress bar */}
                    <div className="mt-auto">
                      <div className="flex justify-between text-xs mb-1">
                        <span className="text-gray-500">Prikupljeno</span>
                        <span className="font-semibold text-green-600">{percentage}%</span>
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-gradient-to-r from-green-500 to-green-600 h-2 rounded-full transition-all duration-500"
                          style={{ width: `${percentage}%` }}
                        />
                      </div>
                    </div>
                  </Link>
                )
              })}
            </div>

            {/* View all button */}
            <div className="text-center">
              <Button
                asChild
                size="lg"
                variant="outline"
                className="border-2 border-red-600 text-red-600 hover:bg-red-50 px-8 py-6 text-lg rounded-full"
              >
                <Link to="/kampanje" className="flex items-center gap-2">
                  Pogledaj sve kampanje
                  <ArrowRight className="w-5 h-5" />
                </Link>
              </Button>
            </div>
          </>
        )}
      </div>
    </section>
  )
}
