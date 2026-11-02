"use client";

import axios from "axios";
import type { AppStore } from "@/app/redux/store";
import { logout } from "@/app/redux/slices/authSlice";

let store: AppStore | null = null;

export function conectarStore(novaStore: AppStore | null): void {
  store = novaStore;
}

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = store?.getState().auth.token;
  const rotaPublica = config.url?.endsWith("/auth/login")
    || config.url?.endsWith("/auth/registrar");

  if (token && !rotaPublica) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

api.interceptors.response.use(
  (resposta) => resposta,
  (erro: unknown) => {
    const sessaoAtiva = Boolean(store?.getState().auth.token);
    if (
      axios.isAxiosError(erro)
      && erro.response?.status === 401
      && !erro.config?.url?.endsWith("/auth/login")
      && !erro.config?.url?.endsWith("/auth/registrar")
      && sessaoAtiva
      && typeof window !== "undefined"
    ) {
      store?.dispatch(logout());
      window.location.assign("/login");
    }
    return Promise.reject(erro);
  },
);

export function mensagemDeErro(erro: unknown, alternativa: string): string {
  if (axios.isAxiosError<{ mensagem?: string }>(erro)) {
    return erro.response?.data?.mensagem ?? alternativa;
  }
  return alternativa;
}

export default api;
