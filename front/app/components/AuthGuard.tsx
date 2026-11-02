"use client";

import { useEffect, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { useAppSelector } from "@/app/redux/hooks";

export default function AuthGuard({ children }: Readonly<{ children: ReactNode }>) {
  const router = useRouter();
  const estado = useAppSelector((state) => state.auth.estado);

  useEffect(() => {
    if (estado === "naoAutenticado") {
      router.replace("/login");
    }
  }, [estado, router]);

  if (estado !== "autenticado") {
    return <main className="p-8 text-sm text-slate-600">Verificando sessão...</main>;
  }

  return children;
}
