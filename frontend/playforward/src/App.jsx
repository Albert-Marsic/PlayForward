import { Routes, Route, useLocation } from "react-router-dom"
import Navbar from "./components/Navbar"
import Home from "./pages/Home"
import Prijava from "./pages/Prijava"
import Page_404 from "./pages/Page_404"
import Registracija from "./pages/Registracija"
import './App.css'

function App() {
  return (
    <>
      <Navbar />

      <main className="pt-16">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/prijava" element={<Prijava />} />
          <Route path="/registracija" element={<Registracija />} />
          <Route path="*" element={<Page_404 />}/>
        </Routes>
      </main>
    </>
  )
}

export default App
