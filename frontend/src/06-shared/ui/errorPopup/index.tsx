import { useEffect, useState } from "react";
import styles from "./index.module.css";

type Props = {
  text: string;
  onClose?: () => void;
  duration?: number;
};

export const ErrorPopup = (
  {
    text,
    onClose,
    duration = 5000
  }: Props
) => {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false);
      onClose?.();
    }, duration);
    
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  if (!visible) return null;

  return (
    <div className={styles.errorMessage}>
      {text}
    </div>
  );
};
