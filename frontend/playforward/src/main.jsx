import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import { NotificationProvider } from './context/NotificationContext.jsx'
import { TalkJSProvider } from './context/TalkJSContext.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <TalkJSProvider>
          <NotificationProvider>
            <App />
          </NotificationProvider>
        </TalkJSProvider>
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
)
