import { useNotification } from "@/context/NotificationContext";
import { X, AlertCircle, CheckCircle, Info } from "lucide-react";

export default function NotificationCenter() {
  const { notifications, removeNotification } = useNotification();

  const getIcon = (type) => {
    switch (type) {
      case "success":
        return <CheckCircle size={20} className="text-green-600" />;
      case "error":
        return <AlertCircle size={20} className="text-red-600" />;
      case "warning":
        return <AlertCircle size={20} className="text-yellow-600" />;
      default:
        return <Info size={20} className="text-blue-600" />;
    }
  };

  const getBgColor = (type) => {
    switch (type) {
      case "success":
        return "bg-green-50 border-green-200";
      case "error":
        return "bg-red-50 border-red-200";
      case "warning":
        return "bg-yellow-50 border-yellow-200";
      default:
        return "bg-blue-50 border-blue-200";
    }
  };

  const getTextColor = (type) => {
    switch (type) {
      case "success":
        return "text-green-700";
      case "error":
        return "text-red-700";
      case "warning":
        return "text-yellow-700";
      default:
        return "text-blue-700";
    }
  };

  return (
    <div className="fixed top-20 right-4 z-50 space-y-2 max-w-md">
      {notifications.map((notif) => (
        <div
          key={notif.id}
          className={`flex items-center gap-3 p-4 border rounded-lg shadow-lg animate-in slide-in-from-top ${getBgColor(
            notif.type
          )} ${getTextColor(notif.type)}`}
        >
          {getIcon(notif.type)}
          <p className="flex-1">{notif.message}</p>
          <button
            onClick={() => removeNotification(notif.id)}
            className="hover:opacity-70 transition"
          >
            <X size={18} />
          </button>
        </div>
      ))}
    </div>
  );
}
