import { useState } from 'react'
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
              <a href="/prijava">Prijava</a>
            </NavigationMenuLink>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
      <h1>PlayForward stranica!</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <div className="flex flex-col items-center justify-center">
          <Button>Click me</Button>
        </div>

      </div>
    </>
  )
}

export default App
