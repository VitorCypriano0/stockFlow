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
