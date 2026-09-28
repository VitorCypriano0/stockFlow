"use client";

import { useEffect, useState, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { obterToken } from "@/app/lib/session";

export default function AuthGuard({ children }: Readonly<{ children: ReactNode }>) {
  const router = useRouter();
  const [autorizado, setAutorizado] = useState(false);

  useEffect(() => {
    // Esconde o painel até confirmar que existe uma sessão no navegador.
    if (!obterToken()) {
      router.replace("/login");
      return;
    }
    setAutorizado(true);
  }, [router]);

  if (!autorizado) {
    return <main className="p-8 text-sm text-slate-600">Verificando sessão...</main>;
  }

  return children;
}
