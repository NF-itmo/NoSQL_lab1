import { useSyncExternalStore } from "react";
import { getOrders } from "../api/orders-api";
import type { Order } from "./types";

let orders: Order[] = [];
let initialization: Promise<void> | null = null;
const listeners = new Set<() => void>();
const expirationTimers = new Map<number, ReturnType<typeof setTimeout>>();

const notify = () => {
  listeners.forEach((listener) => listener());
};

const subscribe = (listener: () => void) => {
  listeners.add(listener);
  return () => {
    listeners.delete(listener);
  };
};

const getSnapshot = () => orders;

const scheduleExpiration = (order: Order) => {
  const currentTimer = expirationTimers.get(order.id);
  if (currentTimer) {
    clearTimeout(currentTimer);
  }

  const delay = Math.max(0, new Date(order.expiresAt).getTime() - Date.now());
  expirationTimers.set(order.id, setTimeout(() => removeOrder(order.id), delay));
};

export const useOrders = () => useSyncExternalStore(subscribe, getSnapshot);

export const initializeOrders = (): Promise<void> => {
  if (!initialization) {
    initialization = getOrders()
      .then((loadedOrders) => {
        orders = loadedOrders;
        orders.forEach(scheduleExpiration);
        notify();
      })
      .catch((error) => {
        initialization = null;
        throw error;
      });
  }

  return initialization;
};

export const addOrder = (order: Order) => {
  orders = [...orders.filter((item) => item.id !== order.id), order];
  scheduleExpiration(order);
  notify();
};

export const removeOrder = (orderId: number) => {
  const timer = expirationTimers.get(orderId);
  if (timer) {
    clearTimeout(timer);
    expirationTimers.delete(orderId);
  }

  orders = orders.filter((order) => order.id !== orderId);
  notify();
};
