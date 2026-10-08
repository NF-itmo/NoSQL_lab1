import { useId } from "react";
import styles from "./index.module.css";

type Props = {
  name: string;
  value?: string;
  setInput: (data: string) => void;
  autoFocus?: boolean;
};

export const PopupTextarea = ({
  name,
  value,
  setInput,
  autoFocus = false,
}: Props) => {
  const id = useId();

  return (
    <div className={styles.textareaWrapper}>
      <textarea
        className={styles.textarea}
        id={id}
        value={value}
        onChange={(event) => setInput(event.target.value)}
        autoFocus={autoFocus}
      />
      <label className={styles.textareaLabel} htmlFor={id}>{name}</label>
    </div>
  );
};
