import { Routes, Route, Link } from "react-router-dom"
import { useState } from "react"
import { Button } from '@/components/ui/button'
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuIndicator,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  NavigationMenuViewport,
} from "@/components/ui/navigation-menu"

import Home from "./pages/Home"
import Prijava from "./pages/Prijava"
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <NavigationMenu className="fixed top-10 shadow-md z-50">
        <NavigationMenuList>
          <NavigationMenuItem>
            <NavigationMenuTrigger>O nama</NavigationMenuTrigger>
            <NavigationMenuContent>
              <NavigationMenuLink>Vizija</NavigationMenuLink>
              <NavigationMenuLink>Legionari</NavigationMenuLink>
            </NavigationMenuContent>
          </NavigationMenuItem>
          <NavigationMenuItem>
            <NavigationMenuLink asChild>
              <Link to="/prijava">Prijava</Link>
            </NavigationMenuLink>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
      
      
      <main className="p-4">
        Router!
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/prijava" element={<Prijava />} />
        </Routes>
      </main>
    </>
  )
}

export default App
