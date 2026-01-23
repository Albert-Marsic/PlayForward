import { useEffect, useRef, useState } from "react";
import { useNotification } from "@/context/NotificationContext";
import { capturePayPalOrder, createPayPalOrder } from "@/api/paypal";

export default function PayPalButton({ requestId, onSuccess, onError }) {
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
    if (!requestId) {
      addNotification("Nedostaje ID zahtjeva za plaćanje", "error");
      return;
    }

    if (paypalContainerRef.current) {
      paypalContainerRef.current.innerHTML = "";
    }

    window.paypal
      .Buttons({
        createOrder: async () => {
          const order = await createPayPalOrder(requestId);
          return order.orderId;
        },

        onApprove: async (data) => {
          try {
            setLoading(true);
            const updated = await capturePayPalOrder(requestId, data.orderID);
            addNotification("Poštarina je uspješno plaćena! 🎉", "success");
            if (onSuccess) onSuccess(updated);
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
  }, [requestId, addNotification, onSuccess, onError]);

  return (
    <div ref={paypalContainerRef} className="my-4">
      {loading && <p className="text-center text-gray-600">Obrada plaćanja...</p>}
    </div>
  );
}
