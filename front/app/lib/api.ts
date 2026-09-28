import axios from "axios";
import { limparSessao, obterToken } from "./session";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// O mesmo cliente Axios inclui o token do login em cada chamada ao backend.
api.interceptors.request.use((config) => {
  const token = obterToken();
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

// Token expirado ou inválido encerra a sessão e retorna para a tela de login.
api.interceptors.response.use(
  (resposta) => resposta,
  (erro: unknown) => {
    if (
      axios.isAxiosError(erro)
      && erro.response?.status === 401
      && !erro.config?.url?.endsWith("/auth/login")
      && typeof window !== "undefined"
    ) {
      limparSessao();
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
