import axios from "axios";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export function mensagemDeErro(erro: unknown, alternativa: string): string {
  if (axios.isAxiosError<{ mensagem?: string }>(erro)) {
    return erro.response?.data?.mensagem ?? alternativa;
  }
  return alternativa;
}
