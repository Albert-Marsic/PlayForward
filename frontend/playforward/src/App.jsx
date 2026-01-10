import { Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import Home from "./pages/Home"
import Prijava from "./pages/Prijava"
import Page_404 from "./pages/Page_404"
import Donacija from "./pages/Donacija"
import Dashboard from "./pages/Dashboard"
import ToysPage from "./pages/ToysPage"
import ToyDetails from "./pages/ToyDetails"
import Kosarica from "./pages/Cart"
import Kupovina from "./pages/CheckoutPage"
import './App.css'

function App() {
  return (
    <>
      <Navbar />

      <main className="pt-16">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/prijava" element={<Prijava />} />
          <Route path="/dashboard" element={<Dashboard />} />
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
