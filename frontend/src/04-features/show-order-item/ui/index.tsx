import { setBookAvailability } from "@/05-models/book";
import { cancelOrder, confirmOrder, removeOrder, type Order } from "@/05-models/order";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import styles from "./index.module.css";

type Props = {
  order: Order;
};

export const ShowOrderItem = ({ order }: Props) => {
  const showError = useErrorNotify();

  const cancel = async () => {
    try {
      await cancelOrder(order.id);
      removeOrder(order.id);
    } catch (error) {
      showError(error);
    }
  };

  const confirm = async () => {
    try {
      await confirmOrder(order.id);
      removeOrder(order.id);
      setBookAvailability(order.bookId, false);
    } catch (error) {
      showError(error);
    }
  };

  return (
    <article className={styles.order}>
      <div>
        <p className={styles.orderTitle}>Заявка #{order.id}</p>
        <p>Книга #{order.bookId}</p>
        <p>
          Истекает: {new Date(order.expiresAt).toLocaleString("ru-RU", {
            dateStyle: "short",
            timeStyle: "short",
          })}
        </p>
      </div>
      <div className={styles.actions}>
        <button onClick={confirm}>Подтвердить</button>
        <button onClick={cancel}>Отменить</button>
      </div>
    </article>
  );
};
