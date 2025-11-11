import { Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import Home from "./pages/Home"
import Prijava from "./pages/Prijava"
import './App.css'

function App() {
  return (
    <>
      <Navbar />

      <main className="pt-16">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/prijava" element={<Prijava />} />
        </Routes>
      </main>
    </>
  )
}

export default App
