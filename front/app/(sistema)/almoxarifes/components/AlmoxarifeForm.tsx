"use client";

import { useEffect, useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import FormMessage from "@/app/components/FormMessage";
import { Field, fieldClasses } from "@/app/components/Field";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Almoxarife, AlmoxarifeInput } from "@/app/types/stockflow";

interface AlmoxarifeFormProps {
  id?: number;
}

const formularioVazio: AlmoxarifeInput = {
  nome: "",
  cpf: "",
  email: "",
  telefone: "",
};

export default function AlmoxarifeForm({ id }: AlmoxarifeFormProps) {
  const router = useRouter();
  const [formulario, setFormulario] = useState<AlmoxarifeInput>(formularioVazio);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(id !== undefined);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (id === undefined) return;

    api
      .get<Almoxarife>("/almoxarifes/" + id)
      .then(({ data }) => {
        setFormulario({
          nome: data.nome,
          cpf: data.cpf,
          email: data.email,
          telefone: data.telefone ?? "",
        });
      })
      .catch((error: unknown) => {
        setErro(mensagemDeErro(error, "Não foi possível carregar o almoxarife."));
      })
      .finally(() => setCarregando(false));
  }, [id]);

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setSalvando(true);

    const dados: AlmoxarifeInput = {
      ...formulario,
      nome: formulario.nome.trim(),
      cpf: formulario.cpf.trim(),
      email: formulario.email.trim(),
      telefone: formulario.telefone?.trim() || null,
    };

    try {
      if (id === undefined) {
        await api.post("/almoxarifes", dados);
      } else {
        await api.put("/almoxarifes/" + id, dados);
      }
      router.push("/almoxarifes");
      router.refresh();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível salvar o almoxarife."));
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return <p className="text-sm text-slate-600">Carregando cadastro...</p>;
  }

  return (
    <form
      onSubmit={salvar}
      className="max-w-3xl rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7"
    >
      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Nome completo">
          <input
            className={fieldClasses}
            value={formulario.nome}
            onChange={(event) =>
              setFormulario({ ...formulario, nome: event.target.value })
            }
            autoComplete="name"
            required
          />
        </Field>
        <Field label="CPF">
          <input
            className={fieldClasses}
            value={formulario.cpf}
            onChange={(event) =>
              setFormulario({ ...formulario, cpf: event.target.value })
            }
            placeholder="000.000.000-00"
            required
          />
        </Field>
        <Field label="E-mail">
          <input
            className={fieldClasses}
            type="email"
            value={formulario.email}
            onChange={(event) =>
              setFormulario({ ...formulario, email: event.target.value })
            }
            autoComplete="email"
            required
          />
        </Field>
        <Field label="Telefone" descricao="Opcional">
          <input
            className={fieldClasses}
            type="tel"
            value={formulario.telefone ?? ""}
            onChange={(event) =>
              setFormulario({ ...formulario, telefone: event.target.value })
            }
            autoComplete="tel"
          />
        </Field>
      </div>

      {erro ? (
        <div className="mt-5">
          <FormMessage texto={erro} />
        </div>
      ) : null}

      <div className="mt-7 flex flex-wrap justify-end gap-3 border-t border-slate-100 pt-5">
        <button
          type="button"
          onClick={() => router.push("/almoxarifes")}
          className="min-h-10 rounded-xl border border-slate-300 px-4 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
        >
          Cancelar
        </button>
        <button
          type="submit"
          disabled={salvando}
          className="min-h-10 rounded-xl bg-emerald-800 px-5 text-sm font-semibold text-white transition hover:bg-emerald-900 disabled:cursor-wait disabled:opacity-60"
        >
          {salvando ? "Salvando..." : "Salvar almoxarife"}
        </button>
      </div>
    </form>
  );
}
