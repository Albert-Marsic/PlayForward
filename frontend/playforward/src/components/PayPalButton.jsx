import { useEffect, useRef, useState } from "react";
import { useNotification } from "@/context/NotificationContext";
import { executePayPalPayment } from "@/api/paypal";

export default function PayPalButton({ amount, description, requestId, onSuccess, onError }) {
  const [loading, setLoading] = useState(false);
  const paypalContainerRef = useRef(null);
  const { addNotification } = useNotification();

  useEffect(() => {
    // Provjeri da li je PayPal SDK učitan
    if (!window.paypal) {
      addNotification("PayPal nije dostupan", "error");
      return;
    }

    // Renderuj PayPal gumb
    window.paypal
      .Buttons({
        createOrder: (data, actions) => {
          return actions.order.create({
            purchase_units: [
              {
                amount: {
                  value: amount.toString(),
                  currency_code: "EUR",
                },
                description: description,
              },
            ],
          });
        },

        onApprove: async (data, actions) => {
          try {
            setLoading(true);

            // Detaljno o sredstvima
            const details = await actions.order.capture();

            // Pošalji na backend
            await executePayPalPayment(details.id, data.payerID);

            addNotification("Plaćanje je uspješno! 🎉", "success");
            if (onSuccess) onSuccess(details);
          } catch (err) {
            console.error("Greška pri obradi plaćanja:", err);
            addNotification("Greška pri obradi plaćanja", "error");
            if (onError) onError(err);
          } finally {
            setLoading(false);
          }
        },

        onError: (err) => {
          console.error("PayPal greška:", err);
          addNotification("PayPal greška: " + err.message, "error");
          if (onError) onError(err);
        },

        onCancel: () => {
          addNotification("Plaćanje je otkazano", "warning");
        },
      })
      .render(paypalContainerRef.current);
  }, [amount, description, addNotification, onSuccess, onError]);

  return (
    <div ref={paypalContainerRef} className="my-4">
      {loading && <p className="text-center text-gray-600">Obrada plaćanja...</p>}
    </div>
  );
}
