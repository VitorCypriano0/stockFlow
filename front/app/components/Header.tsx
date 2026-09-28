"use client";

import { useRouter } from "next/navigation";
import { limparSessao } from "@/app/lib/session";

export default function Header() {
  const router = useRouter();

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
        onClick={() => {
          limparSessao();
          router.replace("/login");
        }}
        className="rounded-xl border border-slate-300 px-3 py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-50"
      >
        Sair
      </button>
    </header>
  );
}
