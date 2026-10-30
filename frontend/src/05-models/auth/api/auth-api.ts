import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { LoginRequest, RegisterRequest } from "../model/types";

const AUTH_API_PATH = `${API_V1_PATH}/auth`;

const jsonHeaders = {
  "Content-Type": "application/json",
};

export const login = async (payload: LoginRequest): Promise<void> => {
  await request<void>(`${AUTH_API_PATH}/login`, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(payload),
  });
};

export const register = async (payload: RegisterRequest): Promise<void> => {
  await request<void>(`${AUTH_API_PATH}/register`, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(payload),
  });
};

export const validateSession = async (): Promise<void> => {
  await request<void>(`${AUTH_API_PATH}/validate`, { method: "POST" });
};

export const logout = async (): Promise<void> => {
  await request<void>(`${AUTH_API_PATH}/logout`, { method: "POST" });
};
