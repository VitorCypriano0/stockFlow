"use client";

import Link from "next/link";
import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import FormMessage from "@/app/components/FormMessage";
import { Field, fieldClasses } from "@/app/components/Field";
import { api, mensagemDeErro } from "@/app/lib/api";
import { salvarToken } from "@/app/lib/session";
import type { AuthResponse } from "@/app/types/stockflow";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [entrando, setEntrando] = useState(false);

  async function entrar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setEntrando(true);

    try {
      const { data } = await api.post<AuthResponse>("/auth/login", {
        email: email.trim(),
        senha,
      });
      salvarToken(data.token);
      router.replace("/home");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível entrar. Confira seus dados."));
    } finally {
      setEntrando(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-[#f4f7f5] px-5 py-10">
      <section className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-7 shadow-sm sm:p-9">
        <Link href="/" className="text-sm font-semibold text-emerald-800 hover:underline">← Stock Flow</Link>
        <p className="mt-8 text-xs font-bold uppercase tracking-[0.18em] text-emerald-700">Acesso ao sistema</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-900">Entrar</h1>
        <p className="mt-2 text-sm leading-6 text-slate-600">Use o e-mail e a senha do seu cadastro de almoxarife.</p>

        <form onSubmit={entrar} className="mt-7 space-y-5">
          <Field label="E-mail">
            <input
              className={fieldClasses}
              type="email"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </Field>
          <Field label="Senha">
            <input
              className={fieldClasses}
              type="password"
              autoComplete="current-password"
              value={senha}
              onChange={(event) => setSenha(event.target.value)}
              required
            />
          </Field>
          {erro ? <FormMessage texto={erro} /> : null}
          <button
            type="submit"
            disabled={entrando}
            className="min-h-11 w-full rounded-xl bg-emerald-900 px-5 text-sm font-semibold text-white transition hover:bg-emerald-800 disabled:cursor-wait disabled:opacity-60"
          >
            {entrando ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-600">
          Primeiro acesso? <Link href="/cadastro" className="font-semibold text-emerald-800 hover:underline">Criar conta</Link>
        </p>
      </section>
    </main>
  );
}
