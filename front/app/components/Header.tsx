"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { api } from "@/app/lib/api";
import { limparSessao } from "@/app/lib/session";

export default function Header() {
  const router = useRouter();
  const [saindo, setSaindo] = useState(false);

  async function sair() {
    setSaindo(true);
    try {
      await api.post("/auth/logout", undefined, { timeout: 5000 });
    } catch {
      // Encerra a sessão local mesmo quando a API estiver indisponível.
    } finally {
      limparSessao();
      router.replace("/login");
    }
  }

  return (
    <header className="flex min-h-16 items-center justify-between border-b border-slate-200 bg-white px-5 sm:px-8">
      <div>
        <p className="text-sm font-semibold text-slate-800">
          Painel do almoxarifado
        </p>
        <p className="text-xs text-slate-500">
          Organize entradas, saídas e saldo dos itens
        </p>
      </div>
      <button
        type="button"
        onClick={() => void sair()}
        disabled={saindo}
        className="rounded-xl border border-slate-300 px-3 py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-wait disabled:opacity-60"
      >
        {saindo ? "Saindo..." : "Sair"}
      </button>
    </header>
  );
}
