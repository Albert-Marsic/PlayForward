import Hero from '@/components/Hero'
import HowItWorks from '@/components/HowItWorks'
import RecentToys from '@/components/RecentToys'
import CampaignsTeaser from '@/components/CampaignsTeaser'
import FinalCTA from '@/components/FinalCTA'
import Footer from '@/components/Footer'

export default function Home() {
  return (
    <div className="min-h-screen">
      <Hero />
      <RecentToys />
      <HowItWorks />
      <CampaignsTeaser />
      <FinalCTA />
      <Footer />
    </div>
  )
}
