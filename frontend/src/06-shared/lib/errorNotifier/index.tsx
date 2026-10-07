import { createNotifier } from "@/06-shared/lib/notifier";
import { ErrorPopup } from "@/06-shared/ui/errorPopup";

const extractErrorMessage = (error: unknown): string | null => {
  if (!error) {
    return null;
  }

  if (typeof error === "string") {
    return error.trim() || null;
  }

  if (error instanceof Error) {
    const message = error.message?.trim();
    if (!message) {
      return null;
    }

    try {
      const parsed = JSON.parse(message);
      return parsed.message.trim() ?? null
    } catch {
      // fallback to the plain message below
    }

    return message;
  }

  return null;
};

export const {
  NotificationProvider: ErrorNotifier,
  useNotifier: useErrorNotification,
} = createNotifier(ErrorPopup);

export const useErrorNotify = () => {
  const { showNotification } = useErrorNotification();

  return (error: unknown) => {
    const message = extractErrorMessage(error);
    if (message) {
      showNotification(message);
    }
  };
};
