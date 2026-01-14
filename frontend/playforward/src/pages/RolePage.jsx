import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import { api } from "@/lib/api";
import { useAuth } from "@/context/AuthContext";

export default function Uloga() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const chooseRole = async (role) => {
    await api("/users/role", {
      method: "POST",
      body: JSON.stringify({ role }),
    });

    if (role === "DONATOR") navigate("/doniraj");
    else navigate("/igracke");
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="bg-white p-8 rounded shadow max-w-md w-full text-center">
        <h1 className="text-2xl font-bold mb-4">
          Dobrodošao{user?.name && `, ${user.name}`}
        </h1>

        <div className="flex flex-col gap-4">
          <Button onClick={() => chooseRole("DONATOR")}>
            🎁 Donator
          </Button>
          <Button variant="outline" onClick={() => chooseRole("PRIMATELJ")}>
            🧸 Primatelj
          </Button>
        </div>
      </div>
    </div>
  );
}
