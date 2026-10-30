import { useState } from "react";
import { addOrder, createOrder } from "@/05-models/order";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { PopupButton, PopupInput, PopupWindow } from "@/06-shared/ui/popups";
import styles from "./index.module.css";

export const CreateOrder = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [bookId, setBookId] = useState<string>("");
  const showError = useErrorNotify();

  const submit = async () => {
    const id = Number(bookId);

    if (!Number.isInteger(id) || id <= 0) {
      showError("ID книги должен быть положительным целым числом");
      return;
    }

    try {
      const order = await createOrder(id);
      addOrder(order);
      setBookId("");
      setIsOpen(false);
    } catch (error) {
      showError(error);
    }
  };

  return (
    <>
      <button className={styles.openButton} onClick={() => setIsOpen(true)}>
        [+]
      </button>

      {isOpen && (
        <PopupWindow name="Новая заявка" onClose={() => setIsOpen(false)}>
          <PopupInput
            name="ID книги"
            value={bookId}
            setInput={setBookId}
            onEnter={submit}
            autoFocus={true}
          />
          <PopupButton name="Добавить" onClick={submit}/>
        </PopupWindow>
      )}
    </>
  );
};
