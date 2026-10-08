import { createContext, useCallback, useContext, useState } from "react";
import type { ComponentType, Context, ReactNode } from "react";


// тип, который обязаны принимать все ноды сообщений
export type NotificationNodeProps = {
  text: string;
  onClose: () => void;
  duration: number;
};

// тип контекста
type NotificationContextValue = {
  showNotification: (text: string, duration?: number) => void;
};

type NotifierProps = {
  children: ReactNode;
  notificationShowDurationMs?: number;
};

type NotifierProviderFactoryProps = {
  notificationContextProvider: Context<NotificationContextValue | undefined>;
  notificationNode: ComponentType<NotificationNodeProps>;
};

const NotificationProviderFactory = (
  {
    notificationContextProvider: NotificationContext,
    notificationNode: NotificationNode
  }: NotifierProviderFactoryProps
) => {
  const NotificationProvider = (
    {
      children,
      notificationShowDurationMs = 5000,
    }: NotifierProps
  ) => {
    const [notificationText, setNotificationText] = useState<string | null>(null);
    const [notificationDuration, setNotificationDuration] = useState(notificationShowDurationMs);

    const showNotification = useCallback((text: string, duration = notificationShowDurationMs) => {
      setNotificationText(text);
      setNotificationDuration(duration);
    }, [notificationShowDurationMs]);

    const handleClose = () => setNotificationText(null);

    return (
      <NotificationContext.Provider value={{ showNotification }}>
        {children}
        {notificationText && (
          <NotificationNode
            text={notificationText}
            onClose={handleClose}
            duration={notificationDuration}
          />
        )}
      </NotificationContext.Provider>
    );
  };

  return NotificationProvider;
};

export const createNotifier = (
  NotificationNode: ComponentType<NotificationNodeProps>
) => {
  const NotificationContext = createContext<NotificationContextValue | undefined>(undefined);
  const NotificationProvider = NotificationProviderFactory({
    notificationContextProvider: NotificationContext,
    notificationNode: NotificationNode
  });

  const useNotifier = () => {
    const context = useContext(NotificationContext);
    if (!context) {
      throw new Error("Notifier provider is missing");
    }
    return context;
  };

  return {
    NotificationProvider,
    useNotifier,
  };
};
