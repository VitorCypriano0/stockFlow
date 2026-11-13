import api from "./api";
import type { Almoxarife, LoginResponse, LoginSession } from "@/app/types/stockflow";

export async function loginBack(email: string, senha: string): Promise<LoginSession> {
  const { data: login } = await api.post<LoginResponse>("/auth/login", { email, senha });
  const { data: almoxarife } = await api.get<Almoxarife>("/auth/me", {
    headers: { Authorization: "Bearer " + login.token },
  });

  return { token: login.token, almoxarife };
}
