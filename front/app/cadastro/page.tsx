"use client";

import Link from "next/link";
import { useState, type FormEvent } from "react";
import FormMessage from "@/app/components/FormMessage";
import { Field, fieldClasses } from "@/app/components/Field";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { AlmoxarifeInput } from "@/app/types/stockflow";

interface CadastroInput extends Omit<AlmoxarifeInput, "senha"> {
  senha: string;
}

const cadastroVazio: CadastroInput = {
  nome: "",
  cpf: "",
  email: "",
  telefone: "",
  senha: "",
};

export default function CadastroPage() {
  const [formulario, setFormulario] = useState<CadastroInput>(cadastroVazio);
  const [erro, setErro] = useState("");
  const [concluido, setConcluido] = useState(false);
  const [salvando, setSalvando] = useState(false);

  async function cadastrar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setSalvando(true);

    try {
      await api.post("/auth/registrar", {
        ...formulario,
        nome: formulario.nome.trim(),
        cpf: formulario.cpf.trim(),
        email: formulario.email.trim(),
        telefone: formulario.telefone?.trim() || null,
      });
      setConcluido(true);
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível criar a conta."));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-[#f4f7f5] px-5 py-10">
      <section className="w-full max-w-xl rounded-3xl border border-slate-200 bg-white p-7 shadow-sm sm:p-9">
        <Link href="/login" className="text-sm font-semibold text-emerald-800 hover:underline">← Voltar ao login</Link>
        <p className="mt-8 text-xs font-bold uppercase tracking-[0.18em] text-emerald-700">Novo acesso</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-900">Criar conta</h1>
        <p className="mt-2 text-sm leading-6 text-slate-600">O cadastro também permite ativar um almoxarife já existente usando o mesmo e-mail e CPF.</p>

        {concluido ? (
          <div className="mt-7 rounded-2xl bg-emerald-50 p-5 text-sm text-emerald-900" role="status">
            <p className="font-semibold">Conta criada com sucesso.</p>
            <p className="mt-1">Agora você já pode entrar com o e-mail e a senha escolhidos.</p>
            <Link href="/login" className="mt-4 inline-flex font-semibold underline">Ir para o login</Link>
          </div>
        ) : (
          <form onSubmit={cadastrar} className="mt-7">
            <div className="grid gap-5 sm:grid-cols-2">
              <Field label="Nome completo">
                <input className={fieldClasses} autoComplete="name" value={formulario.nome} onChange={(event) => setFormulario({ ...formulario, nome: event.target.value })} required />
              </Field>
              <Field label="CPF">
                <input className={fieldClasses} placeholder="000.000.000-00" value={formulario.cpf} onChange={(event) => setFormulario({ ...formulario, cpf: event.target.value })} required />
              </Field>
              <Field label="E-mail">
                <input className={fieldClasses} type="email" autoComplete="email" value={formulario.email} onChange={(event) => setFormulario({ ...formulario, email: event.target.value })} required />
              </Field>
              <Field label="Telefone" descricao="Opcional">
                <input className={fieldClasses} type="tel" autoComplete="tel" value={formulario.telefone ?? ""} onChange={(event) => setFormulario({ ...formulario, telefone: event.target.value })} />
              </Field>
              <div className="sm:col-span-2">
                <Field label="Senha" descricao="Use pelo menos 8 caracteres.">
                  <input className={fieldClasses} type="password" autoComplete="new-password" minLength={8} value={formulario.senha} onChange={(event) => setFormulario({ ...formulario, senha: event.target.value })} required />
                </Field>
              </div>
            </div>
            {erro ? <div className="mt-5"><FormMessage texto={erro} /></div> : null}
            <button type="submit" disabled={salvando} className="mt-7 min-h-11 w-full rounded-xl bg-emerald-900 px-5 text-sm font-semibold text-white transition hover:bg-emerald-800 disabled:cursor-wait disabled:opacity-60">
              {salvando ? "Salvando..." : "Criar conta"}
            </button>
          </form>
        )}
      </section>
    </main>
  );
}
