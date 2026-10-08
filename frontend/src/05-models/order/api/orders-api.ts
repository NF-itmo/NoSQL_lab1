import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { Order } from "../model/types";

const ORDERS_API_PATH = `${API_V1_PATH}/orders`;

export const getOrders = (): Promise<Order[]> => request<Order[]>(ORDERS_API_PATH);

export const getOrder = (orderId: number): Promise<Order> =>
  request<Order>(`${ORDERS_API_PATH}/${orderId}`);

export const createOrder = async (bookId: number): Promise<Order> => {
  return request<Order>(ORDERS_API_PATH, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ bookId })
  });
};

export const confirmOrder = async (orderId: number): Promise<void> => {
  await request<void>(`${ORDERS_API_PATH}/${orderId}/confirm`, { method: "POST" });
};

export const cancelOrder = async (orderId: number): Promise<void> => {
  await request<void>(`${ORDERS_API_PATH}/${orderId}/cancel`, { method: "POST" });
};
