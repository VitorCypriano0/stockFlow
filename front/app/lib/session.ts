"use client";

const CHAVE_TOKEN = "stockflow-token";

// O navegador guarda o JWT da sessão para que o cliente Axios autentique as rotas protegidas.
export function obterToken(): string | null {
  return typeof window === "undefined" ? null : window.localStorage.getItem(CHAVE_TOKEN);
}

export function salvarToken(token: string): void {
  window.localStorage.setItem(CHAVE_TOKEN, token);
}

export function limparSessao(): void {
  window.localStorage.removeItem(CHAVE_TOKEN);
}

export function obterPapel(): "ROLE_ADMIN" | "ROLE_USER" | null {
  const token = obterToken();
  const payload = token?.split(".")[1];
  if (!payload) return null;

  try {
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const claims: unknown = JSON.parse(window.atob(base64));
    if (
      typeof claims === "object"
      && claims !== null
      && "role" in claims
      && (claims.role === "ROLE_ADMIN" || claims.role === "ROLE_USER")
    ) {
      return claims.role;
    }
  } catch {
    return null;
  }

  return null;
}
