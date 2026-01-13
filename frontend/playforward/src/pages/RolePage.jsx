import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"

export default function ChooseRole() {
  const navigate = useNavigate()
  const { user } = useAuth()

  const chooseRole = (role) => {
    // privremeno spremamo u localStorage
    localStorage.setItem("userRole", role)

    // kasnije će ovo ići u backend
    if (role === "DONATOR") {
      navigate("/doniraj")
    } else {
      navigate("/igracke")
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white p-8 rounded-xl shadow max-w-md w-full text-center">
        <h1 className="text-2xl font-bold mb-4">
          Dobrodošao{user?.name ? `, ${user.name}` : ""} 👋
        </h1>

        <p className="text-gray-600 mb-8">
          Odaberi kako želiš koristiti PlayForward
        </p>

        <div className="flex flex-col gap-4">
          <Button
            className="bg-red-600 hover:bg-red-700 text-white py-6 text-lg"
            onClick={() => chooseRole("DONATOR")}
          >
            🎁 Želim donirati igračke
          </Button>

          <Button
            variant="outline"
            className="py-6 text-lg"
            onClick={() => chooseRole("PRIMATELJ")}
          >
            🧸 Tražim igračke
          </Button>
        </div>
      </div>
    </div>
  )
}
