import api from "./api";
import type { AuthResponse } from "@/app/types/stockflow";

export async function loginBack(email: string, senha: string): Promise<AuthResponse> {
  const resposta = await api.post<AuthResponse>("/auth/login", { email, senha });
  return resposta.data;
}
