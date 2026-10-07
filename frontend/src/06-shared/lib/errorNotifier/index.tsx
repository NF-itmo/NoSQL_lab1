import { createNotifier } from "@/06-shared/lib/notifier";
import { ErrorPopup } from "@/06-shared/ui/errorPopup";


export const {
  NotificationProvider: ErrorNotifier,
  useNotifier: useErrorNotification,
} = createNotifier(ErrorPopup);

export const useErrorNotify = () => {
  const { showNotification } = useErrorNotification();

  return { showError: showNotification };
};
