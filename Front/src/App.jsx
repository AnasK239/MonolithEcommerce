import { FaBeer } from 'react-icons/fa'
import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    
  <h1 class="flex items-center justify-center h-screen bg-gray-800 text-white
              text-2xl font-bold">
    Welcome <FaBeer />
  </h1>
    
  )
}

export default App
