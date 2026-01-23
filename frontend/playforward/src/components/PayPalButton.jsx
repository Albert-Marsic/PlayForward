import { useEffect, useRef, useState } from "react";
import { useNotification } from "@/context/NotificationContext";
import { capturePayPalOrder, createPayPalOrder } from "@/api/paypal";
import { API_BASE_URL } from "@/lib/config";

export default function PayPalButton({ requestId, onSuccess, onError }) {
  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState("");
  const paypalContainerRef = useRef(null);
  const { addNotification } = useNotification();

  useEffect(() => {
    let cancelled = false;
    setStatusMessage("");
    const resolvePayPalClientId = async () => {
      if (import.meta.env.VITE_PAYPAL_CLIENT_ID) {
        return { clientId: import.meta.env.VITE_PAYPAL_CLIENT_ID, errorMessage: "" };
      }
      try {
        const response = await fetch(`${API_BASE_URL}/api/config/paypal-client-id`);
        if (!response.ok) {
          return { clientId: "", errorMessage: "PayPal konfiguracija nije dostupna" };
        }
        const data = await response.json();
        const clientId = data?.clientId ?? "";
        return {
          clientId,
          errorMessage: clientId ? "" : "PayPal nije konfiguriran",
        };
      } catch (err) {
        console.warn("Neuspješno dohvaćanje PayPal client ID-a:", err);
        return { clientId: "", errorMessage: "PayPal konfiguracija nije dostupna" };
      }
    };

    const loadPayPalSdk = async () => {
      if (window.paypal) return { status: "ready" };
      const existingScript = document.querySelector("script[src*='paypal.com/sdk/js']");
      if (!existingScript) {
        const { clientId, errorMessage } = await resolvePayPalClientId();
        if (!clientId) {
          return { status: "missing_config", message: errorMessage || "PayPal nije konfiguriran" };
        }
        const script = document.createElement("script");
        script.src = `https://www.paypal.com/sdk/js?client-id=${clientId}&currency=EUR`;
        script.async = true;
        await new Promise((resolve, reject) => {
          script.onload = () => resolve();
          script.onerror = () => reject(new Error("PayPal SDK se nije mogao učitati"));
          document.body.appendChild(script);
        });
        return { status: "ready" };
      }
      await new Promise((resolve, reject) => {
        if (window.paypal) {
          resolve();
          return;
        }
        const timeoutId = setTimeout(() => {
          reject(new Error("PayPal SDK nije učitan na vrijeme"));
        }, 10000);
        existingScript.addEventListener("load", () => {
          clearTimeout(timeoutId);
          resolve();
        }, { once: true });
        existingScript.addEventListener("error", () => {
          clearTimeout(timeoutId);
          reject(new Error("PayPal SDK se nije mogao učitati"));
        }, { once: true });
      });
      return { status: "ready" };
    };

    const renderButton = async () => {
      if (!requestId) {
        addNotification("Nedostaje ID zahtjeva za plaćanje", "error");
        return;
      }

      try {
        const sdkStatus = await loadPayPalSdk();
        if (cancelled) return;
        if (sdkStatus?.status === "missing_config") {
          setStatusMessage(sdkStatus.message);
          return;
        }
        if (!window.paypal) {
          throw new Error("PayPal nije dostupan");
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
      } catch (err) {
        console.error("Greška pri učitavanju PayPal SDK-a:", err);
        addNotification(err.message || "PayPal nije dostupan", "error");
        if (onError) onError(err);
      }
    };

    renderButton();
    return () => {
      cancelled = true;
    };
  }, [requestId, addNotification, onSuccess, onError]);

  return (
    <div ref={paypalContainerRef} className="my-4">
      {statusMessage && (
        <p className="text-center text-sm text-amber-600">{statusMessage}</p>
      )}
      {loading && <p className="text-center text-gray-600">Obrada plaćanja...</p>}
    </div>
  );
}
