import { useEffect, useState } from "react";
import { initializeOrders, useOrders } from "@/05-models/order";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { CreateOrder } from "@/04-features/create-order";
import { ShowOrderItem } from "@/04-features/show-order-item";
import styles from "./index.module.css";

export const OrdersFeed = () => {
  const orders = useOrders();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const showError = useErrorNotify();

  useEffect(() => {
    initializeOrders()
      .catch(showError)
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <section className={styles.feed}>
      <div className={styles.header}>
        <h1 className={styles.title}>Заявки</h1>
        <CreateOrder/>
      </div>

      <div className={styles.content}>
        {
          isLoading && (
            <p className={styles.message}>Загрузка заявок...</p>
          )
        }
        {
          (!isLoading && orders.length === 0) && (
            <p className={styles.message}>Активных заявок нет</p>
          )
        }

        {
          orders.map((order) => (
            <ShowOrderItem
              key={order.id}
              order={order}
            />
          ))
        }
      </div>
    </section>
  );
};