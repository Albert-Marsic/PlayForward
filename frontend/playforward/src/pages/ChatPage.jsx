import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import { MessageCircle, ArrowLeft } from "lucide-react";

export default function ChatPage() {
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  if (!authLoading && !user) {
    navigate("/prijava");
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-6 flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => navigate("/dashboard")}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-5 w-5" />
            Natrag na Dashboard
          </Button>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-8 text-center">
          <MessageCircle className="h-16 w-16 mx-auto mb-4 text-green-600" />
          <h1 className="text-3xl font-bold mb-4">FreeChat Poruke</h1>
          <p className="text-gray-600 mb-6">
            Chat widget je aktivan na svim stranicama. Klikni na chat ikonicu u donjem desnom kutu za slanje poruka.
          </p>
        </div>
      </div>
    </div>
  );
}
