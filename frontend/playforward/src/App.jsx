import { Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import NotificationCenter from "./components/NotificationCenter"
import Home from "./pages/Home"
import Prijava from "./pages/Prijava"
import Page_404 from "./pages/Page_404"
import Donacija from "./pages/Donacija"
import Dashboard from "./pages/Dashboard"
import DonatorDashboard from "./pages/DonatorDashboard"
import PrimateljDashboard from "./pages/PrimateljDashboard"
import ProfilePage from "./pages/ProfilePage"
import ChatPage from "./pages/ChatPage"
import AdminDashboard from "./pages/AdminDashboard"
import ReviewPage from "./pages/ReviewPage"
import CampaignsPage from "./pages/CampaignsPage"
import CampaignDetailsPage from "./pages/CampaignDetailsPage"
import CreateCampaignPage from "./pages/CreateCampaignPage"
import ToysPage from "./pages/ToysPage"
import ToyDetails from "./pages/ToyDetails"
import Kosarica from "./pages/Cart"
import Kupovina from "./pages/CheckoutPage"
import './App.css'

function App() {
  return (
    <>
      <Navbar />
      <NotificationCenter />

      <main className="pt-16">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/prijava" element={<Prijava />} />
          <Route path="/profil" element={<ProfilePage />} />
          <Route path="/poruke" element={<ChatPage />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/moje-donacije" element={<DonatorDashboard />} />
          <Route path="/moji-zahtjevi" element={<PrimateljDashboard />} />
          <Route path="/recenzija/:requestId" element={<ReviewPage />} />
          <Route path="/kampanje" element={<CampaignsPage />} />
          <Route path="/kampanje/novo" element={<CreateCampaignPage />} />
          <Route path="/kampanja/:campaignId" element={<CampaignDetailsPage />} />
          <Route path="/doniraj" element={<Donacija />} />
          <Route path="/igracke" element={<ToysPage />} />
          <Route path="/igracke/:id" element={<ToyDetails />}/>
          <Route path="/kosarica" element={<Kosarica />}/>
          <Route path="/kupovina" element={<Kupovina />}/>
          <Route path="*" element={<Page_404 />}/>
        </Routes>
      </main>
    </>
  )
}

export default App
